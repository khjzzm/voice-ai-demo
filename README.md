# Voice AI Demo with ElevenLabs

This project is a Spring Boot application that demonstrates how to use the ElevenLabs API to perform various voice-related tasks, such as creating voice models, generating speech, and managing voice history.

## Features

- **Create Voice Models:** Train a new voice model by providing an audio file.
- **Generate Speech (TTS):** Convert text into speech using a specified voice model.
- **Download Speech:** Download the generated speech as an MP3 file.
- **Manage Voices:** List available voices and delete custom voice models.
- **View History:** Access the history of generated TTS items.
- **User Info:** Retrieve user account information.
- **Health Check:** A simple endpoint to check if the service is running.

## Tech Stack

- **Java 1.8**
- **Spring Boot 2.2.1.RELEASE**
- **Gradle**
- **ElevenLabs API**

## Prerequisites

- Java 8 or higher
- Gradle 6.x
- An active ElevenLabs account and API key

## Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/voice-ai-demo.git
    cd voice-ai-demo
    ```

2.  **Configure API Key:**
    Open the `src/main/resources/application.properties` file and add your ElevenLabs API key:
    ```properties
    elevenlabs.api.key=YOUR_ELEVENLABS_API_KEY
    ```

3.  **Build the project:**
    ```bash
    ./gradlew build
    ```

4.  **Run the application:**
    ```bash
    ./gradlew bootRun
    ```
    The application will start on `http://localhost:8080`.

## API Endpoints

The following endpoints are available:

- `POST /api/voice/create-model`: Create a new voice model.
- `POST /api/voice/generate-speech`: Generate speech from text.
- `POST /api/voice/generate-speech-download`: Generate and download speech as a file.
- `GET /api/voice/voices`: Get a list of available voices.
- `GET /api/voice/user-voices`: Get a list of user-created voices.
- `GET /api/voice/history`: Get the TTS generation history.
- `DELETE /api/voice/history/{historyItemId}`: Delete a specific TTS item.
- `GET /api/voice/history/{historyItemId}/audio`: Download audio from a history item.
- `GET /api/voice/user-info`: Get user account information.
- `DELETE /api/voice/voices/{voiceId}`: Delete a specific voice model.
- `GET /api/voice/health`: Health check endpoint.

For detailed request and response formats, please refer to the `VoiceController.java` class.