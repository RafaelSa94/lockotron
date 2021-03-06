#include <jni.h>

#include <string.h>

#include <android/log.h>

#include "src/util.c"

#define LOG(fmt, ...) __android_log_print(ANDROID_LOG_INFO, "MOBI-O-TRON-TURBO", fmt, ##__VA_ARGS__)

int getMode(int* values, int size);
int getLessRepeated(int* values, int size);

int Java_com_lockotron_mobi_1o_1tron_util_Statistics_00024Native_getMode(JNIEnv *env, jobject instance, jintArray userIds_)
{
    jint *userIds = (*env)->GetIntArrayElements(env, userIds_, NULL);

    int size = (*env)->GetArrayLength(env, userIds_);
    quickSort(userIds, 0, size-1);
    int result = getMode(userIds, size);
    LOG("Called C most repeated function with return %d", result);

    (*env)->ReleaseIntArrayElements(env, userIds_, userIds, 0);

    return result;
}

int Java_com_lockotron_mobi_1o_1tron_util_Statistics_00024Native_lessRepeated(JNIEnv *env, jclass type, jintArray userIds_)
{
    jint *userIds = (*env)->GetIntArrayElements(env, userIds_, NULL);

    int size = (*env)->GetArrayLength(env, userIds_);
    quickSort(userIds, 0, size-1);
    int result = getLessRepeated(userIds, size);
    LOG("Called C less repeated function with return %d", result);

    (*env)->ReleaseIntArrayElements(env, userIds_, userIds, 0);

    return result;
}