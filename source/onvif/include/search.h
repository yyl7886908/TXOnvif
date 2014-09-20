/************************************************
  Des:   This is the file for onvif search
  Time: 2014-09-17
  Author: Yu Yun Long
 ************************************************/

#ifndef __SEARCH_H__
#define __SEARCH_H__


#ifdef __cplusplus
extern "C"{
#endif

#include "tx_onvif_define.h"

int ONVIF_SEARCH_GetServiceCapabilities(char *username, char *password, char *searchService);





#ifdef __cplusplus
}
#endif

#endif
