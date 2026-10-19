cmake_minimum_required(VERSION 3.22.1)
project(vanguard_mobile LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

add_library(vanguard_runtime STATIC runtime.cpp)
target_include_directories(vanguard_runtime PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
target_compile_options(vanguard_runtime PRIVATE -Wall -Wextra -Wpedantic)

if(ANDROID)
    add_library(vanguard_sp SHARED campaign_jni.cpp)
    target_link_libraries(vanguard_sp PRIVATE vanguard_runtime android log)

    add_library(vanguard_mp SHARED multiplayer_jni.cpp)
    target_link_libraries(vanguard_mp PRIVATE vanguard_runtime android log)
else()
    enable_testing()
    add_executable(vanguard_runtime_test runtime_test.cpp)
    target_link_libraries(vanguard_runtime_test PRIVATE vanguard_runtime)
    add_test(NAME vanguard_runtime_test COMMAND vanguard_runtime_test)
endif()

# Clean-room integration seam. KisakCOD sources are intentionally not vendored by
# this foundation build. A future GPL port can provide an explicit source tree.
set(VANGUARD_KISAKCOD_SOURCE "" CACHE PATH "Path to the pinned KisakCOD checkout")
if(VANGUARD_KISAKCOD_SOURCE)
    if(NOT EXISTS "${VANGUARD_KISAKCOD_SOURCE}/CMakeLists.txt")
        message(FATAL_ERROR "VANGUARD_KISAKCOD_SOURCE does not contain CMakeLists.txt")
    endif()
    message(STATUS "KisakCOD source supplied; platform port targets are not enabled yet")
endif()
