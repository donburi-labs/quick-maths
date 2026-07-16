#include <jni.h>

#include <vector>

#include "AudioEngine.h"

namespace {
    quickmaths::AudioEngine &engine() {
        static quickmaths::AudioEngine instance;
        return instance;
    }
}

extern "C" {

JNIEXPORT jboolean JNICALL
Java_io_github_donburilabs_quickMaths_data_NativeSfxEngine_nativeInit(JNIEnv * /*env*/,
                                                                      jobject /*thiz*/,
                                                                      jint defaultSampleRate,
                                                                      jint defaultFramesPerBurst) {
    return engine().init(defaultSampleRate, defaultFramesPerBurst) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_io_github_donburilabs_quickMaths_data_NativeSfxEngine_nativeLoadSample(JNIEnv *env,
                                                                            jobject /*thiz*/,
                                                                            jint sampleId,
                                                                            jfloatArray monoFrames,
                                                                            jint sampleRate) {
    if (monoFrames == nullptr) {
        return JNI_FALSE;
    }
    const jsize length = env->GetArrayLength(monoFrames);
    if (length < 2) {
        return JNI_FALSE;
    }
    std::vector<float> pcm(static_cast<size_t>(length));
    env->GetFloatArrayRegion(monoFrames, 0, length, pcm.data());
    return engine().loadSample(sampleId, std::move(pcm), sampleRate) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_io_github_donburilabs_quickMaths_data_NativeSfxEngine_nativePlay(JNIEnv * /*env*/,
                                                                      jobject /*thiz*/,
                                                                      jint sampleId, jfloat volume,
                                                                      jfloat rate) {
    return engine().play(sampleId, volume, rate);
}

JNIEXPORT void JNICALL
Java_io_github_donburilabs_quickMaths_data_NativeSfxEngine_nativeSetVolume(JNIEnv * /*env*/,
                                                                           jobject /*thiz*/,
                                                                           jint handle,
                                                                           jfloat volume) {
    engine().setVolume(handle, volume);
}

JNIEXPORT void JNICALL
Java_io_github_donburilabs_quickMaths_data_NativeSfxEngine_nativeSetRate(JNIEnv * /*env*/,
                                                                         jobject /*thiz*/,
                                                                         jint handle, jfloat rate) {
    engine().setRate(handle, rate);
}

JNIEXPORT void JNICALL
Java_io_github_donburilabs_quickMaths_data_NativeSfxEngine_nativeStop(JNIEnv * /*env*/,
                                                                      jobject /*thiz*/,
                                                                      jint handle) {
    engine().stop(handle);
}

JNIEXPORT void JNICALL
Java_io_github_donburilabs_quickMaths_data_NativeSfxEngine_nativeSetForeground(JNIEnv * /*env*/,
                                                                               jobject /*thiz*/,
                                                                               jboolean foreground) {
    engine().setForeground(foreground == JNI_TRUE);
}

}
