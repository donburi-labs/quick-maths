#pragma once

#include <oboe/Oboe.h>

#include <array>
#include <atomic>
#include <cstdint>
#include <memory>
#include <mutex>
#include <unordered_map>
#include <vector>

namespace quickmaths {
    class AudioEngine
            : public oboe::AudioStreamDataCallback, public oboe::AudioStreamErrorCallback {
    public:
        static constexpr int kMaxVoices = 12;
        static constexpr int kInvalidHandle = -1;

        bool init(int32_t defaultSampleRate, int32_t defaultFramesPerBurst);

        bool loadSample(int32_t sampleId, std::vector<float> monoFrames, int32_t sampleRate);

        int32_t play(int32_t sampleId, float volume, float rate);

        void setVolume(int32_t handle, float volume);

        void setRate(int32_t handle, float rate);

        void stop(int32_t handle);

        void setForeground(bool foreground);

        oboe::DataCallbackResult
        onAudioReady(oboe::AudioStream *stream, void *audioData, int32_t numFrames) override;

        void onErrorAfterClose(oboe::AudioStream *stream, oboe::Result error) override;

    private:
        struct Sample {
            std::vector<float> frames;
            int32_t sampleRate = 0;
        };

        struct Voice {
            std::atomic<bool> active{false};
            std::atomic<bool> stopRequested{false};
            std::atomic<float> targetVolume{0.0f};
            std::atomic<float> rate{1.0f};
            const Sample *sample = nullptr;
            double position = 0.0;
            float currentVolume = 0.0f;
            uint32_t generation = 0;
        };

        bool openStream();

        Voice *voiceForHandle(int32_t handle);

        std::mutex mLock;
        std::shared_ptr<oboe::AudioStream> mStream;
        std::unordered_map<int32_t, std::unique_ptr<Sample>> mSamples;
        std::array<Voice, kMaxVoices> mVoices;
        bool mForeground = true;
        bool mInitialized = false;
    };

}
