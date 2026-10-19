#include "jni_helpers.h"
#include "runtime.h"

namespace {
vanguard::Runtime runtime("campaign");
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_vanguard_mobile_engine_CampaignNative_initialize(
    JNIEnv* env, jobject, jstring files, jstring cache) {
    return runtime.initialize(from_jstring(env, files), from_jstring(env, cache));
}
extern "C" JNIEXPORT jboolean JNICALL
Java_com_vanguard_mobile_engine_CampaignNative_start(JNIEnv* env, jobject, jstring request) {
    return runtime.start(from_jstring(env, request));
}
extern "C" JNIEXPORT void JNICALL Java_com_vanguard_mobile_engine_CampaignNative_pause(JNIEnv*,
                                                                                       jobject) {
    runtime.pause();
}
extern "C" JNIEXPORT void JNICALL Java_com_vanguard_mobile_engine_CampaignNative_resume(JNIEnv*,
                                                                                        jobject) {
    runtime.resume();
}
extern "C" JNIEXPORT void JNICALL Java_com_vanguard_mobile_engine_CampaignNative_stop(JNIEnv*,
                                                                                      jobject) {
    runtime.stop();
}
extern "C" JNIEXPORT void JNICALL Java_com_vanguard_mobile_engine_CampaignNative_input(
    JNIEnv*, jobject, jint type, jint code, jfloat x, jfloat y, jfloat value) {
    runtime.input(type, code, x, y, value);
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_vanguard_mobile_engine_CampaignNative_pollEvent(JNIEnv* env, jobject) {
    return nullable_jstring(env, runtime.poll_event());
}
