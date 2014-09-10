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
}
TX_ONVIF_REARCH_DEVICEINFO,*LPTX_ONVIF_REARCH_DEVICEINFO;


#endif
