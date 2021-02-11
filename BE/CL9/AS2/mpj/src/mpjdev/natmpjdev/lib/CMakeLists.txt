cmake_minimum_required(VERSION 2.6)

project(nativempjdev C)

SET(	SOURCE_FILES
			mpjdev_natmpjdev_Comm.c
			mpjdev_natmpjdev_Group.c
			mpjdev_natmpjdev_NativeRecvRequest.c
			mpjdev_natmpjdev_NativeSendRequest.c
			mpjdev_natmpjdev_Intracomm.c
			mpjdev_natmpjdev_Intercomm.c
)
#SET(	CMAKE_LIBRARY_OUTPUT_DIRECTORY $ENV{MPJ_HOME}/lib)
ADD_LIBRARY(nativempjdev SHARED ${SOURCE_FILES})

find_package(MPI)
if(MPI_FOUND)
set(CMAKE_COMPILE_FLAGS ${CMAKE_COMPILE_FLAGS} ${MPI_COMPILE_FLAGS})
set(CMAKE_LINK_FLAGS ${CMAKE_LINK_FLAGS} ${MPI_LINK_FLAGS})

include_directories(${MPI_INCLUDE_PATH} $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/linux)
endif(MPI_FOUND)
target_link_libraries(nativempjdev ${MPI_LIBRARIES})
install(TARGETS nativempjdev DESTINATION $ENV{MPJ_HOME}/lib)

