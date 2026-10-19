#pragma once

#include <jni.h>

#include <string>

inline std::string from_jstring(JNIEnv* env, jstring value) {
    if (value == nullptr) return {};
    const char* chars = env->GetStringUTFChars(value, nullptr);
    if (chars == nullptr) return {};
    std::string result(chars);
    env->ReleaseStringUTFChars(value, chars);
    return result;
}

inline jstring nullable_jstring(JNIEnv* env, const std::string& value) {
    return value.empty() ? nullptr : env->NewStringUTF(value.c_str());
}
