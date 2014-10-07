/************************************************
  Des:   This is the file for onvif media service
  Time: 2014-09-11
  Author: Yu Yun Long
 ************************************************/

#include "include/media.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "sha1.h"
#include "soapH.h"
#include "base64.h"
#include "soapStub.h"
#include "stdsoap2.h"
/* #include "../loghelp.h" */

typedef struct
{
    char username[64];
    char password[32];
}UserInfo_S;

static void ONVIF_GenrateDigest(unsigned char *pwddigest_out, unsigned char *pwd, char *nonc, char *time)
{
    const unsigned char *tdist;
    unsigned char dist[1024] = {0};
    char tmp[1024] = {0};
    unsigned char bout[1024] = {0};
    strcpy(tmp,nonc);
    base64_64_to_bits((char*)bout, tmp);
    sprintf(tmp,"%s%s%s",bout,time,pwd);
    sha1_hash(tmp,dist);
    tdist = dist;
    memset(bout,0x0,1024);
    base64_bits_to_64(bout,tdist,(int)strlen((const char*)tdist));
    strcpy((char *)pwddigest_out,(const char*)bout);
}


static struct soap* ONVIF_Initsoap(struct SOAP_ENV__Header *header, const char *was_To, const char *was_Action, int timeout, UserInfo_S *pUserInfo)
{

	struct soap *soap = NULL; 
	unsigned char macaddr[6];
	char _HwId[1024];
	unsigned int Flagrand;
	soap = soap_new();
	if(soap == NULL)
	{
		printf("[%d]soap = NULL\n", __LINE__);
		return NULL;
	}
	 /* soap_set_namespaces( soap, namespaces); */
	//超过5秒钟没有数据就退出
	if (timeout > 0)
	{
		soap->recv_timeout = timeout;
		soap->send_timeout = timeout;
		soap->connect_timeout = timeout;
	}
	else
	{
		//如果外部接口没有设备默认超时时间的话，我这里给了一个默认值10s
		soap->recv_timeout    = 10;
		soap->send_timeout    = 10;
		soap->connect_timeout = 10;
	}
	soap_default_SOAP_ENV__Header(soap, header);

	// 为了保证每次搜索的时候MessageID都是不相同的！因为简单，直接取了随机值
	srand((int)time(0));
	Flagrand = rand()%9000 + 1000; //保证四位整数
	macaddr[0] = 0x1; macaddr[1] = 0x2; macaddr[2] = 0x3; macaddr[3] = 0x4; macaddr[4] = 0x5; macaddr[5] = 0x6;
	sprintf(_HwId,"urn:uuid:%ud68a-1dd2-11b2-a105-%02X%02X%02X%02X%02X%02X", 
			Flagrand, macaddr[0], macaddr[1], macaddr[2], macaddr[3], macaddr[4], macaddr[5]);
	header->wsa__MessageID =(char *)malloc( 100);
	memset(header->wsa__MessageID, 0, 100);
	strncpy(header->wsa__MessageID, _HwId, strlen(_HwId));

    
	if( pUserInfo != NULL )
	{
	    header->wsse__Security = (struct _wsse__Security *)malloc(sizeof(struct _wsse__Security));
	    memset(header->wsse__Security, 0 , sizeof(struct _wsse__Security));

	    header->wsse__Security->UsernameToken = (struct _wsse__UsernameToken *)calloc(1,sizeof(struct _wsse__UsernameToken));
	    header->wsse__Security->UsernameToken->Username = (char *)malloc(64);
	    memset(header->wsse__Security->UsernameToken->Username, '\0', 64);

	    header->wsse__Security->UsernameToken->Nonce = (char*)malloc(64);
	    memset(header->wsse__Security->UsernameToken->Nonce, '\0', 64);
	    strcpy(header->wsse__Security->UsernameToken->Nonce,"LKqI6G/AikKCQrN0zqZFlg=="); //\u6ce8\u610f\u8fd9\u91cc

	    header->wsse__Security->UsernameToken->wsu__Created = (char*)malloc(64);
	    memset(header->wsse__Security->UsernameToken->wsu__Created, '\0', 64);
	    strcpy(header->wsse__Security->UsernameToken->wsu__Created,"2010-09-16T07:50:45Z");

	    strcpy(header->wsse__Security->UsernameToken->Username, pUserInfo->username);
	    header->wsse__Security->UsernameToken->Password = (struct _wsse__Password *)malloc(sizeof(struct _wsse__Password));
	    header->wsse__Security->UsernameToken->Password->Type = (char*)malloc(128);
	    memset(header->wsse__Security->UsernameToken->Password->Type, '\0', 128);
	    strcpy(header->wsse__Security->UsernameToken->Password->Type,\
	            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest");
	    header->wsse__Security->UsernameToken->Password->__item = (char*)malloc(128);
	    ONVIF_GenrateDigest((unsigned char*)header->wsse__Security->UsernameToken->Password->__item,\
	            (unsigned char*)pUserInfo->password,header->wsse__Security->UsernameToken->Nonce,header->wsse__Security->UsernameToken->wsu__Created);

	}



	if (was_Action != NULL)
	{
		header->wsa__Action =(char *)malloc(1024);
		memset(header->wsa__Action, '\0', 1024);
		strncpy(header->wsa__Action, was_Action, 1024);//"http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe";
	}
	if (was_To != NULL)
	{
		header->wsa__To =(char *)malloc(1024);
		memset(header->wsa__To, '\0', 1024);
		strncpy(header->wsa__To,  was_To, 1024);//"urn:schemas-xmlsoap-org:ws:2005:04:discovery";	
	}
	soap->header = header;
	return soap;
} 

int ONVIF_MEDIA_GetProfiles(char *username, char *password,char *mediaService,  LPTX_ONVIF_PROFILES_INFO profilesInfo)
{
#ifdef DEBUG
    printf(" [%s]-[%d] Search end!  mediaService = %s \n", __func__, __LINE__, mediaService);
#endif
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetProfiles pro_req;
    struct _trt__GetProfilesResponse pro_resq;

        
    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
           
    memset(&header,0,sizeof(header));
   
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
   
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
   
    sprintf(soap_endpoint,"%s",mediaService);

    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetProfiles";

    do
    {
        soap_call___trt__GetProfiles(soap, soap_endpoint, soap_action, &pro_req, &pro_resq);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else   //\u83b7\u53d6\u53c2\u6570\u6210\u529f
        {         
              printf("[%s][%d] Get GetProfiles success !\n", __func__, __LINE__);
              int i;
              int count = pro_resq.__sizeProfiles;
              printf(" pro_resq.__sizeProfiles = %d\n",  count);
              for(i = 0; i<pro_resq.__sizeProfiles; i++)
                  {
/* # ifdef DEBUG */
                    
/*                       printf(" Profiles[%d]->Name=%s \n", i,pro_resq.Profiles->Name); */
/*                       printf(" Profiles[%d]->token=%s \n",i,pro_resq.Profiles->token); */
/*                       printf(" Profiles[%d]->fixed=%s \n", i,pro_resq.Profiles->fixed); */
/*                       printf(" Profiles[%d]->VideoEncoderConfiguration->Name=%s \n", i,pro_resq.Profiles->VideoEncoderConfiguration->Name);   */
/*                       printf(" Profiles[%d]->VideoEncoderConfiguration->token=%s \n", i,pro_resq.Profiles->VideoEncoderConfiguration->token); */
/*                       printf(" Profiles[%d]->VideoEncoderConfiguration->Encoding=%d \n", i,pro_resq.Profiles->VideoEncoderConfiguration->Encoding); */
/*                       printf(" Profiles[%d]->VideoEncoderConfiguration->Resolution->Width=%d \n", i,pro_resq.Profiles->VideoEncoderConfiguration->Resolution->Width); */
/*                       printf(" Profiles[%d]->VideoEncoderConfiguration->Resolution->Height=%d \n", i,pro_resq.Profiles->VideoEncoderConfiguration->Resolution->Height); */
/*                       printf(" Profiles[%d]->VideoEncoderConfiguration->RateControl->FrameRateLimit=%d \n", i,pro_resq.Profiles->VideoEncoderConfiguration->RateControl->FrameRateLimit); */
/*                       printf(" Profiles[%d]->VideoEncoderConfiguration->RateControl->BitrateLimit=%d \n", i,pro_resq.Profiles->VideoEncoderConfiguration->RateControl->BitrateLimit); */
/*                       printf(" Profiles[%d]->VideoEncoderConfiguration->H264->H264Profile=%d \n", i,pro_resq.Profiles->VideoEncoderConfiguration->H264->H264Profile); */
/* #endif */

                      /* 给prfofileInfo结构体赋值 */
                      /* printf(" Profiles  info token = %s\n", (pro_resq.Profiles+i)->token); */
                      /* printf("Profiles  info size = %d\n", profilesInfo->size); */
                      /* printf("Profiles  info name = %s\n", (pro_resq.Profiles+i)->Name); */
                      /* printf("Profiles  info VideoSourceConfiguration token = %s\n", (pro_resq.Profiles+i)->VideoSourceConfiguration->token); */
                      /* printf("Profiles  info VideoSourceConfiguration name = %s\n", (pro_resq.Profiles+i)->VideoSourceConfiguration->Name); */
                      /* printf("Profiles  info VideoSourceConfiguration SourceToken = %s\n", (pro_resq.Profiles+i)->VideoSourceConfiguration->SourceToken); */
                      /*  printf("Profiles  info VideoEncoderConfiguration  name = %s\n", (pro_resq.Profiles+i)->VideoEncoderConfiguration->Name); */
                      /*   printf("Profiles  info VideoEncoderConfiguration  token = %s\n", (pro_resq.Profiles+i)->VideoEncoderConfiguration->token); */


                      sprintf(profilesInfo->token[profilesInfo->size],"%s", (pro_resq.Profiles + i)->token);
                      sprintf(profilesInfo->name[profilesInfo->size],"%s", (pro_resq.Profiles + i)->Name);
                      sprintf(profilesInfo->VSCToken[profilesInfo->size], "%s",(pro_resq.Profiles + i)->VideoSourceConfiguration->token);
                      sprintf(profilesInfo->VSCName[profilesInfo->size], "%s",(pro_resq.Profiles + i)->VideoSourceConfiguration->Name);
                      sprintf(profilesInfo->videoSourceToken[profilesInfo->size], "%s",(pro_resq.Profiles + i)->VideoSourceConfiguration->SourceToken);
                      sprintf(profilesInfo->VECToken[profilesInfo->size], "%s",(pro_resq.Profiles + i)->VideoEncoderConfiguration->token);
                       sprintf(profilesInfo->VECName[profilesInfo->size], "%s",(pro_resq.Profiles + i)->VideoEncoderConfiguration->Name);

                      profilesInfo->size++ ;
                  }
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_MEDIA_GetStreamURI(char *username, char *password, char *deviceService, LPTX_ONVIF_STREAM_URI streamURI)
{
#ifdef DEBUG
    printf(" [%s]-[%d] media.c !  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    int retval = 0;
    int i =0;
    struct soap *soap = NULL;
    const char *soap_action;
    struct _trt__GetStreamUri stream_req;
    struct _trt__GetStreamUriResponse stream_resp;

    
    struct _trt__GetProfiles pro_req;
    struct _trt__GetProfilesResponse pro_resq;

    struct _tds__GetCapabilities capa_req;
    struct _tds__GetCapabilitiesResponse capa_resp;

        
    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
       
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);

    sprintf(soap_endpoint, "%s",deviceService);

    capa_req.Category = (enum tt__CapabilityCategory *)soap_malloc(soap, sizeof(int));
    capa_req.__sizeCategory = 1;
    *(capa_req.Category) = (enum tt__CapabilityCategory)tt__CapabilityCategory__Media;
   
    soap_action = "http://www.onvif.org/ver10/device/wsdl/GetCapabilities";


    do
    {
        soap_call___tds__GetCapabilities(soap, soap_endpoint, soap_action, &capa_req, &capa_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d] Get GetProfiles success !\n", __func__, __LINE__);
        }
    }while(0);

    soap_destroy(soap);
    

    printf("Media endpoint is %s\n",capa_resp.Capabilities->Media->XAddr);
 
    if(capa_resp.Capabilities->Media->XAddr==NULL)
    {
        retval = soap->error;
        return retval;
    }
        
    strcpy(soap_endpoint,capa_resp.Capabilities->Media->XAddr);

    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 10, &stUserInfo);

    soap_action = "http://www.onvif.org/ver10/media/wsdl/GetProfiles";

    do
    {
        soap_call___trt__GetProfiles(soap, soap_endpoint, soap_action, &pro_req, &pro_resq);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else   //\u83b7\u53d6\u53c2\u6570\u6210\u529f
        {         
              printf("[%s][%d] Get GetProfiles success !\n", __func__, __LINE__);
        }
    }while(0);
    

    soap_destroy(soap);

    printf(" profile's size is %d\n",pro_resq.__sizeProfiles);

  
    for(i = 0 ;i <pro_resq.__sizeProfiles;i++)
    {
        strcpy(soap_endpoint,capa_resp.Capabilities->Media->XAddr);
        memset(&header,0,sizeof(header));
        soap = ONVIF_Initsoap(&header, NULL, NULL, 10, &stUserInfo);

        soap_action = "http://www.onvif.org/ver10/media/wsdl/GetStreamUri";
        stream_req.StreamSetup = (struct tt__StreamSetup *)soap_malloc(soap, sizeof(struct tt__StreamSetup));
        stream_req.StreamSetup->Stream = tt__StreamType__RTP_Unicast;
        stream_req.StreamSetup->Transport =  (struct tt__Transport *)soap_malloc(soap, sizeof(struct tt__Transport));
        stream_req.StreamSetup->Transport->Protocol = tt__TransportProtocol__RTSP;
        stream_req.StreamSetup->Transport->Tunnel = NULL;
        stream_req.StreamSetup->__size = 1;
        stream_req.StreamSetup->__any = NULL;
        stream_req.StreamSetup->__anyAttribute = NULL;
        stream_req.ProfileToken = pro_resq.Profiles[i].token;

         do
        {
            printf("endpoint = %s\n", soap_endpoint);
            soap_call___trt__GetStreamUri(soap, soap_endpoint, soap_action, &stream_req, &stream_resp);
            if (soap->error)
            {
                    printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                    retval = soap->error;
                    return retval;
            }
            else
            {         
                  printf("[%s][%d] Get StreamUri success !\n", __func__, __LINE__);
                  //printf(" uri is %s\n",stream_resp.MediaUri->Uri);
            }
        }while(0);       

        soap_destroy(soap);
        printf("Profile <%s> has a stream:\n%s\n", pro_resq.Profiles[i].Name,stream_resp.MediaUri->Uri);
        sprintf(streamURI->name[streamURI->size], "%s",pro_resq.Profiles[i].Name);
        sprintf(streamURI->streamURI[streamURI->size],"%s", stream_resp.MediaUri->Uri);
        streamURI->size ++;
    }
    
    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;

}

int ONVIF_MEDIA_GetServiceCapabilities(char *username, char *password, char *mediaService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetServiceCapabilities media_GetServiceCapabilities_req;
    struct _trt__GetServiceCapabilitiesResponse media_GetServiceCapabilities_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",mediaService);
    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetServiceCapabilities";

    do
    {
        soap_call___trt__GetServiceCapabilities(soap, soap_endpoint, soap_action, &media_GetServiceCapabilities_req, &media_GetServiceCapabilities_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_MEDIA_GetVideoSources(char *username, char *password, char *mediaService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetVideoSources media_GetVideoSources_req;
    struct _trt__GetVideoSourcesResponse media_GetVideoSources_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",mediaService);
    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetVideoSources";

    do
    {
        soap_call___trt__GetVideoSources(soap, soap_endpoint, soap_action, &media_GetVideoSources_req, &media_GetVideoSources_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_MEDIA_GetAudioSources(char *username, char *password, char *mediaService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetAudioSources media_GetAudioSources_req;
    struct _trt__GetAudioSourcesResponse media_GetAudioSources_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",mediaService);
    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetAudioSources";

    do
    {
        soap_call___trt__GetAudioSources(soap, soap_endpoint, soap_action, &media_GetAudioSources_req, &media_GetAudioSources_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_MEDIA_GetProfile(char *username, char *password, char *mediaService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetProfile media_GetProfile_req;
    struct _trt__GetProfileResponse media_GetProfile_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",mediaService);
    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetProfile";

    do
    {
        soap_call___trt__GetProfile(soap, soap_endpoint, soap_action, &media_GetProfile_req, &media_GetProfile_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}


int ONVIF_MEDIA_GetVideoSourceConfigurations(char *username, char *password, char *mediaService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetVideoSourceConfigurations media_GetVideoSourceConfigurations_req;
    struct _trt__GetVideoSourceConfigurationsResponse media_GetVideoSourceConfigurations_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",mediaService);
    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetVideoSourceConfigurations";

    do
    {
        soap_call___trt__GetVideoSourceConfigurations(soap, soap_endpoint, soap_action, &media_GetVideoSourceConfigurations_req, &media_GetVideoSourceConfigurations_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_MEDIA_GetVideoEncoderConfigurations(char *username, char *password, char *mediaService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetVideoEncoderConfigurations media_GetVideoEncoderConfigurations_req;
    struct _trt__GetVideoEncoderConfigurationsResponse media_GetVideoEncoderConfigurations_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",mediaService);
    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetVideoEncoderConfigurations";

    do
    {
        soap_call___trt__GetVideoEncoderConfigurations(soap, soap_endpoint, soap_action, &media_GetVideoEncoderConfigurations_req, &media_GetVideoEncoderConfigurations_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_MEDIA_GetAudioSourceConfigurations(char *username, char *password, char *mediaService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetAudioSourceConfigurations media_GetAudioSourceConfigurations_req;
    struct _trt__GetAudioSourceConfigurationsResponse media_GetAudioSourceConfigurations_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",mediaService);
    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetAudioSourceConfigurations";

    do
    {
        soap_call___trt__GetAudioSourceConfigurations(soap, soap_endpoint, soap_action, &media_GetAudioSourceConfigurations_req, &media_GetAudioSourceConfigurations_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_MEDIA_GetAudioEncoderConfigurations(char *username, char *password, char *mediaService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetAudioEncoderConfigurations media_GetAudioEncoderConfigurations_req;
    struct _trt__GetAudioEncoderConfigurationsResponse media_GetAudioEncoderConfigurations_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",mediaService);
    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetAudioSourceConfigurations";

    do
    {
        soap_call___trt__GetAudioEncoderConfigurations(soap, soap_endpoint, soap_action, &media_GetAudioEncoderConfigurations_req, &media_GetAudioEncoderConfigurations_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

int ONVIF_MEDIA_GetVideoAnalyticsConfigurations(char *username, char *password, char *mediaService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _trt__GetVideoAnalyticsConfigurations media_GetVideoAnalyticsConfigurations_req;
    struct _trt__GetVideoAnalyticsConfigurationsResponse media_GetVideoAnalyticsConfigurations_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s",mediaService);
    const char *soap_action = "http://www.onvif.org/ver10/media/wsdl/GetVideoAnalyticsConfigurations";

    do
    {
        soap_call___trt__GetVideoAnalyticsConfigurations(soap, soap_endpoint, soap_action, &media_GetVideoAnalyticsConfigurations_req, &media_GetVideoAnalyticsConfigurations_resp);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else  
        {         
              printf("[%s][%d]   success !\n", __func__, __LINE__);
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}
