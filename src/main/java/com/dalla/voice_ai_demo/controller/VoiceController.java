package com.dalla.voice_ai_demo.controller;

import com.dalla.voice_ai_demo.service.ElevenLabsService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/voice")
@CrossOrigin(origins = "*")
public class VoiceController {

    @Autowired
    private ElevenLabsService elevenLabsService;

    /**
     * 음성 모델 생성 (사용자 목소리 학습)
     */
    @PostMapping("/create-model")
    public ResponseEntity<Map<String, Object>> createVoiceModel(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam("audioFile") MultipartFile audioFile) {
        
        try {
            String voiceId = elevenLabsService.createVoiceModel(name, description, language, audioFile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("voiceId", voiceId);
            response.put("message", "음성 모델이 성공적으로 생성되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "음성 모델 생성 실패: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * TTS 생성 (학습된 목소리로 텍스트를 음성으로 변환) - 고급 옵션 지원
     */
    @PostMapping("/generate-speech")
    public ResponseEntity<Map<String, Object>> generateSpeech(
            @RequestParam("voiceId") String voiceId,
            @RequestParam("text") String text,
            @RequestParam(value = "modelId", required = false) String modelId,
            @RequestParam(value = "languageCode", required = false) String languageCode,
            @RequestParam(value = "stability", required = false) Double stability,
            @RequestParam(value = "similarityBoost", required = false) Double similarityBoost,
            @RequestParam(value = "style", required = false) Double style,
            @RequestParam(value = "useSpeakerBoost", required = false) Boolean useSpeakerBoost,
            @RequestParam(value = "outputFormat", required = false) String outputFormat,
            @RequestParam(value = "enableLogging", required = false) Boolean enableLogging,
            @RequestParam(value = "optimizeStreamingLatency", required = false) Integer optimizeStreamingLatency,
            @RequestParam(value = "applyTextNormalization", required = false) String applyTextNormalization,
            @RequestParam(value = "applyLanguageTextNormalization", required = false) Boolean applyLanguageTextNormalization,
            @RequestParam(value = "seed", required = false) Integer seed,
            @RequestParam(value = "previousText", required = false) String previousText,
            @RequestParam(value = "nextText", required = false) String nextText) {
        
        try {
            Map<String, Object> options = new HashMap<>();
            
            // 기본 옵션들
            if (modelId != null) options.put("model_id", modelId);
            if (languageCode != null) options.put("language_code", languageCode);
            if (stability != null) options.put("stability", stability);
            if (similarityBoost != null) options.put("similarity_boost", similarityBoost);
            if (style != null) options.put("style", style);
            if (useSpeakerBoost != null) options.put("use_speaker_boost", useSpeakerBoost);
            if (outputFormat != null) options.put("output_format", outputFormat);
            if (enableLogging != null) options.put("enable_logging", enableLogging);
            if (optimizeStreamingLatency != null) options.put("optimize_streaming_latency", optimizeStreamingLatency);
            if (applyTextNormalization != null) options.put("apply_text_normalization", applyTextNormalization);
            if (applyLanguageTextNormalization != null) options.put("apply_language_text_normalization", applyLanguageTextNormalization);
            if (seed != null) options.put("seed", seed);
            if (previousText != null) options.put("previous_text", previousText);
            if (nextText != null) options.put("next_text", nextText);
            
            byte[] audioData = elevenLabsService.generateSpeech(voiceId, text, options);
            
            // Base64로 인코딩하여 URL 형태로 제공
            String base64Audio = java.util.Base64.getEncoder().encodeToString(audioData);
            String dataUrl = "data:audio/mpeg;base64," + base64Audio;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("audioUrl", dataUrl);
            response.put("audioData", base64Audio);
            response.put("message", "음성이 성공적으로 생성되었습니다.");
            response.put("options", options);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "음성 생성 실패: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * TTS 생성 - 파일 다운로드용 (고급 옵션 지원)
     */
    @PostMapping("/generate-speech-download")
    public ResponseEntity<byte[]> generateSpeechDownload(
            @RequestParam("voiceId") String voiceId,
            @RequestParam("text") String text,
            @RequestParam(value = "filename", defaultValue = "speech.mp3") String filename,
            @RequestParam(value = "modelId", required = false) String modelId,
            @RequestParam(value = "languageCode", required = false) String languageCode,
            @RequestParam(value = "stability", required = false) Double stability,
            @RequestParam(value = "similarityBoost", required = false) Double similarityBoost,
            @RequestParam(value = "style", required = false) Double style,
            @RequestParam(value = "useSpeakerBoost", required = false) Boolean useSpeakerBoost,
            @RequestParam(value = "outputFormat", required = false) String outputFormat,
            @RequestParam(value = "enableLogging", required = false) Boolean enableLogging,
            @RequestParam(value = "optimizeStreamingLatency", required = false) Integer optimizeStreamingLatency,
            @RequestParam(value = "applyTextNormalization", required = false) String applyTextNormalization,
            @RequestParam(value = "applyLanguageTextNormalization", required = false) Boolean applyLanguageTextNormalization,
            @RequestParam(value = "seed", required = false) Integer seed,
            @RequestParam(value = "previousText", required = false) String previousText,
            @RequestParam(value = "nextText", required = false) String nextText) {
        
        try {
            Map<String, Object> options = new HashMap<>();
            
            // 기본 옵션들
            if (modelId != null) options.put("model_id", modelId);
            if (languageCode != null) options.put("language_code", languageCode);
            if (stability != null) options.put("stability", stability);
            if (similarityBoost != null) options.put("similarity_boost", similarityBoost);
            if (style != null) options.put("style", style);
            if (useSpeakerBoost != null) options.put("use_speaker_boost", useSpeakerBoost);
            if (outputFormat != null) options.put("output_format", outputFormat);
            if (enableLogging != null) options.put("enable_logging", enableLogging);
            if (optimizeStreamingLatency != null) options.put("optimize_streaming_latency", optimizeStreamingLatency);
            if (applyTextNormalization != null) options.put("apply_text_normalization", applyTextNormalization);
            if (applyLanguageTextNormalization != null) options.put("apply_language_text_normalization", applyLanguageTextNormalization);
            if (seed != null) options.put("seed", seed);
            if (previousText != null) options.put("previous_text", previousText);
            if (nextText != null) options.put("next_text", nextText);
            
            byte[] audioData = elevenLabsService.generateSpeech(voiceId, text, options);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(audioData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 사용 가능한 음성 모델 목록 조회
     */
    @GetMapping("/voices")
    public ResponseEntity<JsonNode> getVoices() {
        try {
            JsonNode voices = elevenLabsService.getVoices();
            return ResponseEntity.ok(voices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 사용자가 생성한 음성 모델 목록 조회
     */
    @GetMapping("/user-voices")
    public ResponseEntity<JsonNode> getUserVoices() {
        try {
            JsonNode voices = elevenLabsService.getUserVoices();
            return ResponseEntity.ok(voices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * TTS 생성 히스토리 조회
     */
    @GetMapping("/history")
    public ResponseEntity<JsonNode> getTtsHistory() {
        try {
            JsonNode history = elevenLabsService.getTtsHistory();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 TTS 항목 삭제
     */
    @DeleteMapping("/history/{historyItemId}")
    public ResponseEntity<Void> deleteTtsItem(@PathVariable String historyItemId) {
        try {
            elevenLabsService.deleteTtsItem(historyItemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * TTS 히스토리 오디오 파일 다운로드
     */
    @GetMapping("/history/{historyItemId}/audio")
    public ResponseEntity<Resource> downloadTtsAudio(@PathVariable String historyItemId) {
        try {
            byte[] audioData = elevenLabsService.downloadTtsAudio(historyItemId);
            ByteArrayResource resource = new ByteArrayResource(audioData);
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"tts_audio_" + historyItemId + ".mp3\"")
                    .header("Content-Type", "audio/mpeg")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 사용자 계정 정보 조회
     */
    @GetMapping("/user-info")
    public ResponseEntity<JsonNode> getUserInfo() {
        try {
            JsonNode userInfo = elevenLabsService.getUserInfo();
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 음성 모델 삭제
     */
    @DeleteMapping("/voices/{voiceId}")
    public ResponseEntity<Map<String, Object>> deleteVoice(@PathVariable String voiceId) {
        try {
            boolean success = elevenLabsService.deleteVoice(voiceId);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "음성 모델이 성공적으로 삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "음성 모델 삭제에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "음성 모델 삭제 실패: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 헬스 체크
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Voice AI Demo 서비스가 정상적으로 실행 중입니다.");
        
        return ResponseEntity.ok(response);
    }
} 