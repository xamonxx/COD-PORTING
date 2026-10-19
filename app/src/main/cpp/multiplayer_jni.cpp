#include "jni_helpers.h"
#include "runtime.h"

namespace {
vanguard::Runtime runtime("multiplayer");
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_vanguard_mobile_engine_MultiplayerNative_initialize(
    JNIEnv* env, jobject, jstring files, jstring cache) {
    return runtime.initialize(from_jstring(env, files), from_jstring(env, cache));
}
extern "C" JNIEXPORT jboolean JNICALL
Java_com_vanguard_mobile_engine_MultiplayerNative_start(JNIEnv* env, jobject, jstring request) {
    return runtime.start(from_jstring(env, request));
}
extern "C" JNIEXPORT void JNICALL Java_com_vanguard_mobile_engine_MultiplayerNative_pause(JNIEnv*,
                                                                                          jobject) {
    runtime.pause();
}
extern "C" JNIEXPORT void JNICALL
Java_com_vanguard_mobile_engine_MultiplayerNative_resume(JNIEnv*, jobject) {
    runtime.resume();
}
extern "C" JNIEXPORT void JNICALL Java_com_vanguard_mobile_engine_MultiplayerNative_stop(JNIEnv*,
                                                                                         jobject) {
    runtime.stop();
}
extern "C" JNIEXPORT void JNICALL Java_com_vanguard_mobile_engine_MultiplayerNative_input(
    JNIEnv*, jobject, jint type, jint code, jfloat x, jfloat y, jfloat value) {
    runtime.input(type, code, x, y, value);
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_vanguard_mobile_engine_MultiplayerNative_pollEvent(JNIEnv* env, jobject) {
    return nullable_jstring(env, runtime.poll_event());
}
