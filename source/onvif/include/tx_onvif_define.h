/************************************************
  Des:   This is the file for onvif sdk
  Time: 2014-09-10
  Author: Yu Yun Long
 ************************************************/

#ifndef __TX_ONVIF_STRUCT_H__
#define __TX_ONVIF_STRUCT_H__

#include <time.h>
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

/* device */
typedef enum
    {
        tx_factory_default_type_hard = 0,
        tx_factory_default_type_soft = 1,
    }
    TX_FACTORY_DEFAULT_TYPE;

typedef enum
    {
        tx_user_level_administrator = 0,
        tx_user_level_operator = 1,
        tx_user_level_user = 2,
        tx_user_level_anonymous = 3,
        tx_user_level_extended = 4,
    }
    TX_USER_LEVEL;

typedef enum
    {
        tx_onvif_false = 0,
        tx_onvif_true = 1,
    }
    TX_ONVIF_BOOLEAN;


typedef enum
{
    tx_onvif_ptz_move = 0,
    tx_onvif_ptz_zoom = 1,
}
	TX_ONVIF_PTZ_Type;


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
  char deviceio[128];
} 
TX_ONVIF_CAPABILITY_URI, *LPTX_ONVIF_CAPABILITY_URI;

typedef enum
{
	capability_all = 0,
	capability_analytics = 1,
	capability_device = 2,
	capability_event = 3,
	capability_imaging = 4,
	capability_media = 5,
	capability_ptz = 6,
  capability_deviceio = 7
} TX_Capability_Type;

typedef struct 
{
	char manufacturer[64];	
	char model[64];	
	char firmwareVersion[64];
	char serialNumber[64];	
	char hardwareId[64];
}TX_ONVIF_DEVICE_INFO, *LPTX_ONVIF_DEVICE_INFO;

typedef struct  
{
	int size;
	char token[3][32];
	char name[3][32];
	
	char VSCToken[3][32];
	char VSCName[3][32];
	char videoSourceToken[3][32];
	
	char VECToken[3][32];
	char VECName[3][32];
}
TX_ONVIF_PROFILES_INFO, *LPTX_ONVIF_PROFILES_INFO;

typedef struct
{
    int size;
    char name[2][128];
    char streamURI[2][128];
}TX_ONVIF_STREAM_URI, *LPTX_ONVIF_STREAM_URI;


/* imaging  setting*/
typedef enum
    {
        tx_onvif_backlight_compensation_mode_off = 0,
        tx_onvif_backlight_compensation_mode_on,
    }
    TX_ONVIF_BACKLIGHT_COMPENSATION_MODE;

typedef enum
    {
        tx_onvif_exposuremode_auto = 0,
        tx_onvif_exposuremode_manual = 1,
    }
    TX_ONVIF_EXPOSURE_MODE;

typedef enum 
    {
        tx_onvif_exposure_priority_low = 0,
        tx_onvif_exposure_priority_framerate = 1,
    }
    TX_ONIVF_EXPOSURE_PRIORITY;

typedef struct
{
    enum TX_ONVIF_BACKLIGHT_COMPENSATION_MODE* mode;
    float level;
}
    TX_ONVIF_BACKLIGHT_COMPENSATION20;

typedef struct
{
    float bottom;
    float top;
    float right;
    float left;
}
    TX_ONVIF_RECTANGLE;

typedef struct 
{
    enum TX_ONVIF_EXPOSURE_MODE* mode;
    enum TX_ONIVF_EXPOSURE_PRIORITY *priority;
    struct TX_ONVIF_RECTANGLE *window;
    float min_exposure_time;
    float max_exposure_time;
    float min_gain;
    float max_gain;
    float min_lris;
    float max_lris;
    float exposure_time;
    float gain;
    float lris;
}
    TX_ONVIF_EXPOSURE20;

typedef enum
    {
        tx_auto_focus_mode_auto = 0,
        tx_auto_focus_mode_manual,
    }
    TX_ONVIF_AUTO_FOCUS_MODE;

typedef enum
    {
        tx_onvif_lr_cutfilter_mode_on = 0,
        tx_onvif_lr_cutfilter_mode_off = 0,
        tx_onvif_lr_cutfilter_mode_auto = 0,
    }
    TX_ONVIF_IR_CUTFILTER_MODE;

typedef struct
{
    enum TX_ONVIF_AUTO_FOCUS_MODE *mode;
    float default_speed;
    float _near_limit;
    float far_limit;
    /* struct tt__FocusConfiguration20Extension */
}
    TX_ONVIF_FOCUS_CONFIGURATION20;

typedef enum
    {
        tx_onvif_wide_dynamic_mode_on = 0,
        tx_onvif_wide_dynamic_mode_off = 1,
    }
    TX_ONVIF_WIDE_DYNAMIC_MODE;

typedef struct
{
    enum TX_ONVIF_WIDE_DYNAMIC_MODE* mode;
    float level;
}
    TX_ONVIF_WIDE_DYNAMIC_RANGE20;

typedef enum
    {
        tx_onvif_white_balance_mode_auto = 0,
        tx_onvif_white_balance_mode_manual = 1,
    }
    TX_ONVIF_WHITE_BALANCE_MODE;

typedef struct 
{
    enum  TX_ONVIF_WHITE_BALANCE_MODE *mode;
    float crgain;
    float cbgain;
    /* struct tt__WhiteBalance20Extension*  Extension */
}
    TX_ONVIF_WHITE_BALANCE20;

typedef struct 
{
    struct TX_ONVIF_BACKLIGHT_COMPENSATION20 *backlight_compensation;
    float brightness;
    float colorSaturation;
    float contrast;
    struct TX_ONVIF_EXPOSURE20 *exposure;
    struct TX_ONVIF_FOCUS_CONFIGURATION20 *focus;
    enum TX_ONVIF_IR_CUTFILTER_MODE *cutfileter_mode;
    float shapness;
    struct TX_ONVIF_WIDE_DYNAMIC_RANGE20 *wide_dynamic_range;
    struct TX_ONVIF_WHITE_BALANCE20    *white_balance;
    /* struct tt__ImagingSettingsExtension20*  Extension */
}
    TX_ONVIF_IMAGING_SETTINGS20, *LPTX_ONVIF_IMAGING_SETTINGS20;

typedef struct
{
    float brightness;             /* 亮度 */
    float colorSaturation;     /* 色度 */
    float contrast;                 /* 对比度 */
}
    TX_ONVIF_IMAGING_SETTINGS, *LPTX_ONVIF_IMAGING_SETTINGS;

/* ptz */


/*event*/

typedef struct
{
    char Address[128];             
    time_t CurrentTime;     
    time_t TerminationTime;           
}
    TX_ONVIF_EVENT_SUBSCRIPTION, *LPTX_ONVIF_EVENT_SUBSCRIPTION;


#endif
