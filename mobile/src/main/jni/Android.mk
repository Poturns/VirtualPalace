LOCAL_PATH := $(call my-dir)

#include $(CLEAR_VARS)
#LOCAL_MODULE := static_modules
#LOCAL_SRC_FILES = ./libmodules.a
#include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

#opencv
OPENCVROOT:= $(LOCAL_PATH)/../../../../openCVLibrary300/src/main/
OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES := off
OPENCV_LIB_TYPE := STATIC
include ${OPENCVROOT}/jni/OpenCV.mk


LOCAL_MODULE := shell
#LOCAL_SRC_FILES := shell.cpp
LOCAL_SRC_FILE := shell.cpp

LOCAL_C_INCLUDES := ${OPENCVROOT}/jni/include
LOCAL_C_INCLUDES += $(LOCAL_PATH)
#LOCAL_STATIC_LIBRARIES := static_modules
LOCAL_LDLIBS +=  -llog -ldl
APP_CFLAGS := -g

include $(BUILD_SHARED_LIBRARY)