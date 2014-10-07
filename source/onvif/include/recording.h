/************************************************
  Des:   This is the file for onvif recording
  Time: 2014-09-124
  Author: Yu Yun Long
 ************************************************/

#ifndef __RECORDING_H__
#define __RECORDING_H__


#ifdef __cplusplus
extern "C"{
#endif

#include "tx_onvif_define.h"

int ONVIF_RECORDING_GetServiceCapabilities(char *username, char *password, char *recordingService);

int ONVIF_RECORDING_GetRecordings(char *username, char *password, char *recordingService);

int ONVIF_RECORDING_GetRecordingConfiguration(char *username, char *password, char *recordingService, char* recordToken);

int ONVIF_RECORDING_GetTrackConfiguration(char *username, char *password, char *recordingService, char *recordToken, char *trackToken);

int ONVIF_RECORDING_CreateRecording(char *username, char *password, char *recordingService);

#ifdef __cplusplus
}
#endif

#endif
