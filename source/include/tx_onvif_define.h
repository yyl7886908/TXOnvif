/************************************************
  Des:   This is the file for onvif sdk
  Time: 2014-09-010
  Author: Yu Yun Long
 ************************************************/

#ifndef __TX_ONVIF_STRUCT_H__
#define __TX_ONVIF_STRUCT_H__

#ifndef bool

#define bool int
#endif
#ifndef true
#define true 1
#endif
#ifndef false
#define false 0
#endif

/* error code */

#define OK										(0)
#define COMMON_ERROR				(-1)


/* struct define */

typedef struct 
{
	char XAddrs[128];
	char Types[128];
	char Address[128];
	char item[1024];
    int MetadataVersion;
}
TX_ONVIF_REARCH_DEVICEINFO,*LPTX_ONVIF_REARCH_DEVICEINFO;

typedef struct  
{
	char analytics[128];
	char device[128];
	char events[128];
	char imaging[128];
	char media[128];
	char ptz[128];
} TX_ONVIF_CAPABILITY_URI, *LPTX_ONVIF_CAPABILITY_URI;

typedef enum
{
	capability_all = 0,
	capability_analytics = 1,
	capability_device = 2,
	capability_event = 3,
	capability_imaging = 4,
	capability_media = 5,
	capability_ptz = 6
} TX_Capability_Type;



#endif
