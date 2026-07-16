cmake_minimum_required(VERSION 3.22.1)
project(quickmaths_audio LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(oboe REQUIRED CONFIG)

add_library(quickmaths_audio SHARED
        AudioEngine.cpp
        JniBridge.cpp)

target_compile_options(quickmaths_audio PRIVATE -Wall)

target_link_libraries(quickmaths_audio
        PRIVATE
        oboe::oboe
        android
        log)
