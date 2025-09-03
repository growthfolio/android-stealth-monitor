cmake_minimum_required(VERSION 3.22.1)

project("stealthmonitor")

add_library(stealthmonitor SHARED
    native_monitor.c
    jni_bridge.c)

find_library(log-lib log)

target_link_libraries(stealthmonitor ${log-lib})