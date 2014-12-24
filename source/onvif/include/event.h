/************************************************
  Des:   This is the file for onvif device
  Time: 2014-09-17
  Author: Yu Yun Long
 ************************************************/

#ifndef __EVENT_H__
#define __EVENT_H__


#ifdef __cplusplus
extern "C"{
#endif

#include "tx_onvif_define.h"

int ONVIF_EVENT_GetEventProperties(char *username, char *password, char *deviceService);

//ONVIF_GetEventProperties() 


#ifdef __cplusplus
}
#endif

#endif