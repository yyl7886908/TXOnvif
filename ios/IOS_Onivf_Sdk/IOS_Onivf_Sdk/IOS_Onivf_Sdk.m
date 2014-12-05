//
//  IOS_Onivf_Sdk.m
//  IOS_Onivf_Sdk
//
//  Created by taixin on 14-12-4.
//  Copyright (c) 2014年 taixin. All rights reserved.
//

#import "IOS_Onivf_Sdk.h"
#import "tx_onvif_sdk.h"
#import "tx_onvif_define.h"

#define	MULTICAST_IP           "239.255.255.250"
#define	MULTICAST_PORT      	  3702

@implementation IOS_Onivf_Sdk

+ (IOS_Onivf_Sdk *)sharedManager
{
    static IOS_Onivf_Sdk *sharedIOS_Onivf_SdkInstance = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        sharedIOS_Onivf_SdkInstance = [[self alloc] init];
    });
    return sharedIOS_Onivf_SdkInstance;
}


- (NSMutableArray *) discoverDevices
{
    NSMutableArray *deviceArray = [[NSMutableArray alloc]init];
    
    TX_ONVIF_REARCH_DEVICEINFO struDiscoveryDeviceInfos[16];
    int deviceNum = 0;
    int ret = TX_ONVIF_Discovery(MULTICAST_IP,  MULTICAST_PORT, struDiscoveryDeviceInfos, &deviceNum) ;
    NSLog(@"%s:%d discoverDevices ret=%d, deviceNum = %d\n", __func__, __LINE__, ret, deviceNum);
    for(int i = 0; i<deviceNum; i++)
    {
        Device *device = [[Device alloc] init];
        device.XAddrs =[NSString stringWithFormat:@"%s", struDiscoveryDeviceInfos[i].XAddrs];
        device.Types =[NSString stringWithFormat:@"%s", struDiscoveryDeviceInfos[i].Types];
        device.Address =[NSString stringWithFormat:@"%s", struDiscoveryDeviceInfos[i].Address];
        device.item =[NSString stringWithFormat:@"%s", struDiscoveryDeviceInfos[i].item];
        device.MetadataVersion = struDiscoveryDeviceInfos[i].MetadataVersion;
        [deviceArray addObject:device];
        
    }
    return deviceArray;
}

- (DeviceCapability *) getDeviceCapabilities:(NSString *)username
                              password:(NSString *)password
                         deviceService:(NSString *)deviceService
{
    TX_ONVIF_CAPABILITY_URI capabilityInfo;
	memset(&capabilityInfo, 0, sizeof(TX_ONVIF_CAPABILITY_URI));
    const char *user_name = [username cStringUsingEncoding:NSASCIIStringEncoding];
    const char *pass_word = [password cStringUsingEncoding:NSASCIIStringEncoding];
    const char *device_service = [deviceService cStringUsingEncoding:NSASCIIStringEncoding];

	int ret=TX_ONVIF_DEVICE_GetCapabilities(user_name, pass_word, capability_all, device_service, &capabilityInfo);
    NSLog(@"%s:%d getDeviceCapabilities ret=%d\n", __func__, __LINE__, ret);
    if(ret == 0){
        DeviceCapability *capa = [[DeviceCapability alloc] init];
        capa.analytics = [NSString stringWithFormat:@"%s", capabilityInfo.analytics];
        capa.deviceService = [NSString stringWithFormat:@"%s", capabilityInfo.device];
        capa.eventsService = [NSString stringWithFormat:@"%s", capabilityInfo.events];
        capa.imagingService = [NSString stringWithFormat:@"%s", capabilityInfo.imaging];
        capa.mediaService = [NSString stringWithFormat:@"%s", capabilityInfo.media];
        capa.ptzService = [NSString stringWithFormat:@"%s", capabilityInfo.ptz];
        return capa;
        
    }
    return NULL;
}

/*获取多媒体配置参数*/
- (NSMutableArray *) getMediaProfiles:(NSString *)username
                             password:(NSString *)password
                         mediaService:(NSString *)mediaService
{
    TX_ONVIF_PROFILES_INFO profileInfo;
    memset(&profileInfo, 0, sizeof(TX_ONVIF_PROFILES_INFO));
    const char *user_name = [username cStringUsingEncoding:NSASCIIStringEncoding];
    const char *pass_word = [password cStringUsingEncoding:NSASCIIStringEncoding];
    const char *media_service = [mediaService cStringUsingEncoding:NSASCIIStringEncoding];
    
	int ret = TX_ONVIF_MEDIA_GetProfiles((char*)user_name, (char*)pass_word,  (char*)media_service, &profileInfo);
    NSLog(@"%s:%d getMediaProfiles ret=%d\n", __func__, __LINE__, ret);
    if(ret == 0){
        NSMutableArray *mediaProfilesArray = [[NSMutableArray alloc]init];
        for (int i = 0; i < profileInfo.size; i++) {
            MediaProfilesInfo *pro = [[MediaProfilesInfo alloc ]init];
            pro.token =[NSString stringWithFormat:@"%s",  profileInfo.token[i]];
            pro.name =[NSString stringWithFormat:@"%s",  profileInfo.name[i]];
            pro.VSCToken =[NSString stringWithFormat:@"%s",  profileInfo.VSCToken[i]];
            pro.VSCName =[NSString stringWithFormat:@"%s",  profileInfo.VSCName[i]];
            pro.videoSourceToken =[NSString stringWithFormat:@"%s",  profileInfo.videoSourceToken[i]];
            pro.VECToken =[NSString stringWithFormat:@"%s",  profileInfo.VECToken[i]];
            pro.VECName =[NSString stringWithFormat:@"%s",  profileInfo.VECName[i]];
            [mediaProfilesArray addObject:pro];
        }
        NSLog(@"%s:%d getMediaProfiles mediaProfilesArray size=%lu\n", __func__, __LINE__, (unsigned long)[mediaProfilesArray count]);
        return  mediaProfilesArray;
    }
    return NULL;
}

/*获取多媒体视频流*/
- (NSMutableArray *) getMediaStreamUri:(NSString *)username
                              password:(NSString *)password
                          mediaService:(NSString *)mediaService
{
    TX_ONVIF_STREAM_URI streamURI;
    memset(&streamURI, 0, sizeof(TX_ONVIF_STREAM_URI));
    const char *user_name = [username cStringUsingEncoding:NSASCIIStringEncoding];
    const char *pass_word = [password cStringUsingEncoding:NSASCIIStringEncoding];
    const char *media_service = [mediaService cStringUsingEncoding:NSASCIIStringEncoding];
    
	int ret = TX_ONVIF_MEDIA_GetStreamURI((char*)user_name, (char*)pass_word,  (char*)media_service, &streamURI);
    NSLog(@"%s:%d getMediaStreamUri ret=%d\n", __func__, __LINE__, ret);
    if(ret == 0){
        NSMutableArray *mediaStreamUrisArray = [[NSMutableArray alloc]init];
        for (int i = 0; i<streamURI.size; i++) {
            MediaStreamUri *uri = [[MediaStreamUri alloc] init];
            uri.name = [NSString stringWithFormat:@"%s",  streamURI.name[i]];
            uri.streamURI = [NSString stringWithFormat:@"%s",  streamURI.streamURI[i]];
            [mediaStreamUrisArray addObject:uri];
        }
        NSLog(@"%s:%d getMediaProfiles mediaStreamUrisArray size=%lu\n", __func__, __LINE__, (unsigned long)[mediaStreamUrisArray count]);
        return mediaStreamUrisArray;
    }
    return NULL;
}

/*ptz 停止*/
- (int) ptzStop:(NSString *)username
       password:(NSString *)password
     ptzService:(NSString *)ptzService
   profileToken:(NSString *)profileToken
        ptzType:(int)ptzType
{
    int ret = -1;
    const char *user_name = [username cStringUsingEncoding:NSASCIIStringEncoding];
    const char *pass_word = [password cStringUsingEncoding:NSASCIIStringEncoding];
    const char *ptz_service = [ptzService cStringUsingEncoding:NSASCIIStringEncoding];
    const char *profile_token = [profileToken cStringUsingEncoding:NSASCIIStringEncoding];
    
    ret = TX_ONVIF_PTZ_Stop((char*)user_name, (char*)pass_word,  (char*)ptz_service,  (char*)profile_token, (int)ptzType);
    NSLog(@"%s:%d ptzStop ret=%d\n", __func__, __LINE__, ret);
    return ret;
}

/*ptz 移动*/
- (int)ptzContinuousMove:(NSString *)username
                password:(NSString *)password
              ptzService:(NSString *)ptzService
            profileToken:(NSString *)profileToken
                 ptzType:(int)ptzType
                       x:(float)x
                       y:(float)y
                       z:(float)z
{
    NSLog(@"%s:%d ptzContinuousMove x=%f, y=%f, z=%f\n", __func__, __LINE__, x, y ,z);
    int ret = -1;
    const char *user_name = [username cStringUsingEncoding:NSASCIIStringEncoding];
    const char *pass_word = [password cStringUsingEncoding:NSASCIIStringEncoding];
    const char *ptz_service = [ptzService cStringUsingEncoding:NSASCIIStringEncoding];
    const char *profile_token = [profileToken cStringUsingEncoding:NSASCIIStringEncoding];
    
    ret = TX_ONVIF_PTZ_ContinuousMove((char*)user_name, (char*)pass_word,  (char*)ptz_service,  (char*)profile_token, (int)ptzType, (float)x, (float)y, (float)z);
    NSLog(@"%s:%d ptzContinuousMove ret=%d\n", __func__, __LINE__, ret);
    return ret;
    
}

/*获取图像属性*/
- (ImagingSetting *) getImagingSetting:(NSString *)username
                              password:(NSString *)password
                        imagingService:(NSString *)imagingService
                      videoSourceToken:(NSString *)videoSourceToken
{
    TX_ONVIF_IMAGING_SETTINGS imagingSettings;
    memset(&imagingSettings, 0, sizeof(TX_ONVIF_IMAGING_SETTINGS));
    int ret = -1;
    const char *user_name = [username cStringUsingEncoding:NSASCIIStringEncoding];
    const char *pass_word = [password cStringUsingEncoding:NSASCIIStringEncoding];
    const char *image_service = [imagingService cStringUsingEncoding:NSASCIIStringEncoding];
    const char *videoSource_Token = [videoSourceToken cStringUsingEncoding:NSASCIIStringEncoding];

    ret=TX_ONVIF_IMAGING_GetImagingSettings2((char*)user_name, (char*)pass_word, (char*)image_service, (char*)videoSource_Token, &imagingSettings);
    NSLog(@"%s:%d getImagingSetting ret=%d\n", __func__, __LINE__, ret);
    if(ret == 0){
        ImagingSetting *img  = [[ImagingSetting alloc] init];
        img.brightness = imagingSettings.brightness;
        img.colorSaturation = imagingSettings.colorSaturation;
        img.contrast = imagingSettings.contrast;
        return  img;
    }
    return NULL;
}

/*设置图像属性*/
- (int) setImagingSetting:(NSString *)username
                 password:(NSString *)password
           imagingService:(NSString *)imagingService
         videoSourceToken:(NSString *)videoSourceToken
               imgSetting:(ImagingSetting *)imgSetting
{
    int ret = -1;
    const char *user_name = [username cStringUsingEncoding:NSASCIIStringEncoding];
    const char *pass_word = [password cStringUsingEncoding:NSASCIIStringEncoding];
    const char *image_service = [imagingService cStringUsingEncoding:NSASCIIStringEncoding];
    const char *videoSource_Token = [videoSourceToken cStringUsingEncoding:NSASCIIStringEncoding];
    
    TX_ONVIF_IMAGING_SETTINGS imagingSettings;
    memset(&imagingSettings, 0, sizeof(TX_ONVIF_IMAGING_SETTINGS));
    imagingSettings.brightness = (float)imgSetting.brightness;
    imagingSettings.colorSaturation =  (float)imgSetting.colorSaturation;
    imagingSettings.contrast =  (float)imgSetting.contrast;
    ret=TX_ONVIF_IMAGING_SetImagingSettings((char*)user_name, (char*)pass_word, (char*)image_service, (char*)videoSource_Token, &imagingSettings);
    NSLog(@"%s:%d setImagingSetting ret=%d\n", __func__, __LINE__, ret);
    return ret;
}
@end
