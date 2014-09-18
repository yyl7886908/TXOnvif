/************************************************
  Des:   This is the file for onvif media service
  Time: 2014-09-11
  Author: Yu Yun Long
 ************************************************/

#ifndef __MEDIA_H__
#define __MEDIA_H__

#ifdef __cplusplus
extern "C" {
#endif

#include "tx_onvif_define.h"

int ONVIF_MEDIA_GetProfiles(char *username, char *password, char *mediaService,  LPTX_ONVIF_PROFILES_INFO profilesInfo);

int ONVIF_MEDIA_GetStreamURI(char *username, char *password, char *deviceService, LPTX_ONVIF_STREAM_URI streamURI);


/* media */
int ONVIF_MEDIA_GetServiceCapabilities(char *username, char *password, char *mediaService);

int ONVIF_MEDIA_GetVideoSources(char *username, char *password, char *mediaService);

int ONVIF_MEDIA_GetAudioSources(char *username, char *password, char *mediaService);

int ONVIF_MEDIA_GetProfile(char *username, char *password, char *mediaService);

int ONVIF_MEDIA_CreateProfile(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_AddVideoEncoderConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_AddVideoSourceConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_AddAudioEncoderConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_AddAudioSourceConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_AddPTZConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_AddVideoAnalyticsConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_AddMetadataConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_AddAudioDecoderConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_AddAudioOutputConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_RemoveVideoEncoderConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_RemoveVideoSourceConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_RemoveAudioEncoderConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_RemoveAudioSourceConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_RemovePTZConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_RemoveVideoAnalyticsConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_RemoveMetadataConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_RemoveAudioOutputConfiguration(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_DeleteProfile(char *username, char *password, char *mediaService);/* 此接口暂时未封装 */

int ONVIF_MEDIA_GetVideoSourceConfigurations(char *username, char *password, char *mediaService);

int ONVIF_MEDIA_GetVideoEncoderConfigurations(char *username, char *password, char *mediaService);

int ONVIF_MEDIA_GetAudioSourceConfigurations(char *username, char *password, char *mediaService);

int ONVIF_MEDIA_GetAudioEncoderConfigurations(char *username, char *password, char *mediaService);

int ONVIF_MEDIA_GetVideoAnalyticsConfigurations(char *username, char *password, char *mediaService);





#ifdef __cplusplus
}
#endif

#endif
