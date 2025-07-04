package com.dalla.voice_ai_demo.service;

import com.dalla.voice_ai_demo.config.ElevenLabsConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ElevenLabsService {

    @Autowired
    private ElevenLabsConfig config;

    @Autowired
    private RestTemplate elevenLabsRestTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 음성 모델 생성 (사용자 목소리 학습)
     */
    public String createVoiceModel(String name, String description, String language, MultipartFile audioFile) throws IOException {
        String url = config.getBaseUrl() + "/voices/add";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("xi-api-key", config.getApiKey());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("name", name);
        body.add("description", description);
        
        // 언어 정보가 제공된 경우 labels에 추가
        if (language != null && !language.trim().isEmpty()) {
            Map<String, String> labels = new HashMap<>();
            labels.put("language", language);
            body.add("labels", labels);
        }
        
        body.add("files", new ByteArrayResource(audioFile.getBytes()) {
            @Override
            public String getFilename() {
                return audioFile.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<String> response = elevenLabsRestTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            return jsonResponse.get("voice_id").asText();
        } else {
            throw new RuntimeException("음성 모델 생성 실패: " + response.getBody());
        }
    }

    /**
     * TTS 생성 (학습된 목소리로 텍스트를 음성으로 변환) - 고급 옵션 지원
     */
    public byte[] generateSpeech(String voiceId, String text, Map<String, Object> options) {
        String url = config.getBaseUrl() + "/text-to-speech/" + voiceId;
        
        // 쿼리 파라미터 처리
        StringBuilder urlBuilder = new StringBuilder(url);
        boolean firstParam = true;
        
        if (options.containsKey("enable_logging")) {
            urlBuilder.append(firstParam ? "?" : "&").append("enable_logging=").append(options.get("enable_logging"));
            firstParam = false;
        }
        
        if (options.containsKey("optimize_streaming_latency")) {
            urlBuilder.append(firstParam ? "?" : "&").append("optimize_streaming_latency=").append(options.get("optimize_streaming_latency"));
            firstParam = false;
        }
        
        if (options.containsKey("output_format")) {
            urlBuilder.append(firstParam ? "?" : "&").append("output_format=").append(options.get("output_format"));
            firstParam = false;
        }
        
        if (options.containsKey("apply_text_normalization")) {
            urlBuilder.append(firstParam ? "?" : "&").append("apply_text_normalization=").append(options.get("apply_text_normalization"));
            firstParam = false;
        }
        
        if (options.containsKey("apply_language_text_normalization")) {
            urlBuilder.append(firstParam ? "?" : "&").append("apply_language_text_normalization=").append(options.get("apply_language_text_normalization"));
            firstParam = false;
        }
        
        if (options.containsKey("use_pvc_as_ivc")) {
            urlBuilder.append(firstParam ? "?" : "&").append("use_pvc_as_ivc=").append(options.get("use_pvc_as_ivc"));
            firstParam = false;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("xi-api-key", config.getApiKey());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        
        // 기본값 설정
        requestBody.put("model_id", options.getOrDefault("model_id", "eleven_multilingual_v2"));
        
        // 선택적 파라미터들
        if (options.containsKey("language_code")) {
            requestBody.put("language_code", options.get("language_code"));
        }
        
        if (options.containsKey("voice_settings")) {
            requestBody.put("voice_settings", options.get("voice_settings"));
        } else {
            // 기본 voice settings
            Map<String, Object> voiceSettings = new HashMap<>();
            voiceSettings.put("stability", options.getOrDefault("stability", 0.5));
            voiceSettings.put("similarity_boost", options.getOrDefault("similarity_boost", 0.5));
            voiceSettings.put("style", options.getOrDefault("style", 0.0));
            voiceSettings.put("use_speaker_boost", options.getOrDefault("use_speaker_boost", true));
            requestBody.put("voice_settings", voiceSettings);
        }
        
        if (options.containsKey("pronunciation_dictionary_locators")) {
            requestBody.put("pronunciation_dictionary_locators", options.get("pronunciation_dictionary_locators"));
        }
        
        if (options.containsKey("seed")) {
            requestBody.put("seed", options.get("seed"));
        }
        
        if (options.containsKey("previous_text")) {
            requestBody.put("previous_text", options.get("previous_text"));
        }
        
        if (options.containsKey("next_text")) {
            requestBody.put("next_text", options.get("next_text"));
        }
        
        if (options.containsKey("previous_request_ids")) {
            requestBody.put("previous_request_ids", options.get("previous_request_ids"));
        }
        
        if (options.containsKey("next_request_ids")) {
            requestBody.put("next_request_ids", options.get("next_request_ids"));
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<byte[]> response = elevenLabsRestTemplate.exchange(
                urlBuilder.toString(), HttpMethod.POST, requestEntity, byte[].class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("TTS 생성 실패: " + response.getStatusCode());
        }
    }

    /**
     * TTS 생성 (기본 옵션) - 기존 호환성 유지
     */
    public byte[] generateSpeech(String voiceId, String text) {
        Map<String, Object> options = new HashMap<>();
        options.put("model_id", "eleven_multilingual_v2");
        options.put("stability", 0.5);
        options.put("similarity_boost", 0.5);
        options.put("style", 0.0);
        options.put("use_speaker_boost", true);
        options.put("enable_logging", true);
        options.put("output_format", "mp3_44100_128");
        options.put("apply_text_normalization", "auto");
        options.put("apply_language_text_normalization", false);
        
        return generateSpeech(voiceId, text, options);
    }

    /**
     * 사용 가능한 음성 모델 목록 조회
     */
    public JsonNode getVoices() {
        String url = config.getBaseUrl() + "/voices";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getApiKey());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = elevenLabsRestTemplate.exchange(
                url, HttpMethod.GET, requestEntity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper.readTree(response.getBody());
            } catch (Exception e) {
                throw new RuntimeException("음성 목록 조회 실패", e);
            }
        } else {
            throw new RuntimeException("음성 목록 조회 실패: " + response.getStatusCode());
        }
    }

    /**
     * 사용자가 생성한 음성 모델 목록 조회
     */
    public JsonNode getUserVoices() {
        String url = config.getBaseUrl() + "/voices";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getApiKey());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = elevenLabsRestTemplate.exchange(
                url, HttpMethod.GET, requestEntity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode allVoices = objectMapper.readTree(response.getBody());
                // 사용자 생성 음성만 필터링 (category가 'cloned'인 것들)
                return allVoices;
            } catch (Exception e) {
                throw new RuntimeException("사용자 음성 목록 조회 실패", e);
            }
        } else {
            throw new RuntimeException("사용자 음성 목록 조회 실패: " + response.getStatusCode());
        }
    }

    /**
     * TTS 생성 히스토리 조회
     */
    public JsonNode getTtsHistory() {
        String url = config.getBaseUrl() + "/history";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getApiKey());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = elevenLabsRestTemplate.exchange(
                url, HttpMethod.GET, requestEntity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                // ElevenLabs API 응답 구조에 맞게 처리
                if (responseBody.has("history") && responseBody.get("history").has("history")) {
                    // { "history": { "history": [...], "last_history_item_id": "...", "has_more": false } }
                    return responseBody;
                } else {
                    // 배열 형태로 직접 반환된 경우
                    ObjectNode result = objectMapper.createObjectNode();
                    result.set("history", responseBody);
                    return result;
                }
            } catch (Exception e) {
                throw new RuntimeException("TTS 히스토리 조회 실패", e);
            }
        } else {
            throw new RuntimeException("TTS 히스토리 조회 실패: " + response.getStatusCode());
        }
    }

    /**
     * 특정 TTS 항목 삭제
     */
    public void deleteTtsItem(String historyItemId) {
        String url = config.getBaseUrl() + "/history/" + historyItemId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getApiKey());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = elevenLabsRestTemplate.exchange(
                url, HttpMethod.DELETE, requestEntity, String.class);
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("TTS 항목 삭제 실패: " + response.getStatusCode());
        }
    }

    /**
     * TTS 히스토리 오디오 파일 다운로드
     */
    public byte[] downloadTtsAudio(String historyItemId) {
        String url = config.getBaseUrl() + "/history/" + historyItemId + "/audio";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getApiKey());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<byte[]> response = elevenLabsRestTemplate.exchange(
                url, HttpMethod.GET, requestEntity, byte[].class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("TTS 오디오 다운로드 실패: " + response.getStatusCode());
        }
    }

    /**
     * 사용자 계정 정보 조회
     */
    public JsonNode getUserInfo() {
        String url = config.getBaseUrl() + "/user";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getApiKey());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = elevenLabsRestTemplate.exchange(
                url, HttpMethod.GET, requestEntity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper.readTree(response.getBody());
            } catch (Exception e) {
                throw new RuntimeException("사용자 정보 조회 실패", e);
            }
        } else {
            throw new RuntimeException("사용자 정보 조회 실패: " + response.getStatusCode());
        }
    }

    /**
     * 특정 음성 모델 삭제
     */
    public boolean deleteVoice(String voiceId) {
        String url = config.getBaseUrl() + "/voices/" + voiceId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", config.getApiKey());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = elevenLabsRestTemplate.exchange(
                url, HttpMethod.DELETE, requestEntity, String.class);
        
        return response.getStatusCode() == HttpStatus.OK;
    }

} 