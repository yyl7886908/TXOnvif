//
//  IOS_Onivf_Sdk.h
//  IOS_Onivf_Sdk
//
//  Created by taixin on 14-12-4.
//  Copyright (c) 2014年 taixin. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Device.h"
#import "DeviceInfo.h"
#import "DeviceCapability.h"
#import "MediaProfilesInfo.h"
#import "MediaStreamUri.h"
#import "ImagingSetting.h"

@interface IOS_Onivf_Sdk : NSObject
/*单例模式*/
+ (IOS_Onivf_Sdk *) sharedManager;

/*搜索设备*/
- (NSMutableArray *) discoverDevices;

/*获取设备能力*/
- (DeviceInfo *) getDeviceCapabilities:(NSString *)username
                              password:(NSString *)password
                         deviceService:(NSString *)deviceService;

/*获取多媒体配置参数*/
- (NSMutableArray *) getMediaProfiles:(NSString *)username
                             password:(NSString *)password
                         mediaService:(NSString *)mediaService;

/*获取多媒体视频流*/
- (NSMutableArray *) getMediaStreamUri:(NSString *)username
                              password:(NSString *)password
                          mediaService:(NSString *)mediaService;
/*ptz stop*/
- (int) ptzStop:(NSString *)username
       password:(NSString *)password
     ptzService:(NSString *)ptzService
   profileToken:(NSString *)profileToken
        ptzType:(int)ptzType;

/*ptz 移动*/
- (int)ptzContinuousMove:(NSString *)username
                password:(NSString *)password
              ptzService:(NSString *)ptzService
            profileToken:(NSString *)profileToken
                 ptzType:(int)ptzType
                       x:(float)x
                       y:(float)y
                       z:(float)z;

/*获取图像属性*/
- (ImagingSetting *) getImagingSetting:(NSString *)username
                              password:(NSString *)password
                            imagingService:(NSString *)imagingService
                          videoSourceToken:(NSString *)videoSourceToken;

/*设置图像属性*/
- (int) setImagingSetting:(NSString *)username
                 password:(NSString *)password
           imagingService:(NSString *)imagingService
         videoSourceToken:(NSString *)videoSourceToken
               imgSetting:(ImagingSetting *)imgSetting;



@end
