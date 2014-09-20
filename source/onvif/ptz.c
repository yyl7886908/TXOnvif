/************************************************
  Des:   This is the file for onvif ptz
  Time: 2014-09-17
  Author: Yu Yun Long
 ************************************************/

#include "include/ptz.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "soapH.h"
#include "soapStub.h"
#include "stdsoap2.h"
#include "base64.h"
#include "sha1.h"

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

int ONVIF_PTZ_GetServiceCapabilities(char *username, char *password, char *ptzService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__GetServiceCapabilities ptz_GetServiceCapabilities_req;
    struct _tptz__GetServiceCapabilitiesResponse ptz_GetServiceCapabilities_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint,"%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/GetServiceCapabilities";

    do
    {
        soap_call___tptz__GetServiceCapabilities(soap, soap_endpoint, soap_action, &ptz_GetServiceCapabilities_req, &ptz_GetServiceCapabilities_resp);
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

int ONVIF_PTZ_GetConfigurations(char *username, char *password, char *ptzService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__GetConfigurations ptz_GetConfigurations_req;
    struct _tptz__GetConfigurationsResponse ptz_GetConfigurations_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/GetConfigurations";

    do
    {
        soap_call___tptz__GetConfigurations(soap, soap_endpoint, soap_action, &ptz_GetConfigurations_req, &ptz_GetConfigurations_resp);
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

int ONVIF_PTZ_GetPresets(char *username, char *password, char *ptzService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__GetPresets ptz_GetPresets_req;
    struct _tptz__GetPresetsResponse ptz_GetPresets_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/GetPresets";

    do
    {
        soap_call___tptz__GetPresets(soap, soap_endpoint, soap_action, &ptz_GetPresets_req, &ptz_GetPresets_resp);
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

int ONVIF_PTZ_GetStatus(char *username, char *password, char *ptzService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__GetStatus ptz_GetStatus_req;
    struct _tptz__GetStatusResponse ptz_GetStatus_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/GetStatus";

    do
    {
        soap_call___tptz__GetStatus(soap, soap_endpoint, soap_action, &ptz_GetStatus_req, &ptz_GetStatus_resp);
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

int ONVIF_PTZ_GetConfiguration(char *username, char *password, char *ptzService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__GetConfiguration ptz_GetConfiguration_req;
    struct _tptz__GetConfigurationResponse ptz_GetConfiguration_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/GetConfiguration";

    do
    {
        soap_call___tptz__GetConfiguration(soap, soap_endpoint, soap_action, &ptz_GetConfiguration_req, &ptz_GetConfiguration_resp);
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

int ONVIF_PTZ_GetNodes(char *username, char *password, char *ptzService)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__GetNodes ptz_GetNodes_req;
    struct _tptz__GetNodesResponse ptz_GetNodes_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/GetNodes";

    do
    {
        soap_call___tptz__GetNodes(soap, soap_endpoint, soap_action, &ptz_GetNodes_req, &ptz_GetNodes_resp);
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

int ONVIF_PTZ_GetNode(char *username, char *password, char *ptzService, char *nodeToken)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__GetNode ptz_GetNode_req;
    struct _tptz__GetNodeResponse ptz_GetNode_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/GetNode";

    ptz_GetNode_req.NodeToken = nodeToken;
    do
    {
        soap_call___tptz__GetNode(soap, soap_endpoint, soap_action, &ptz_GetNode_req, &ptz_GetNode_resp);
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

int ONVIF_PTZ_GetConfigurationOptions(char *username, char *password, char *ptzService, char *configurationToken)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__GetConfigurationOptions ptz_GetConfigurationOptions_req;
    struct _tptz__GetConfigurationOptionsResponse ptz_GetConfigurationOptions_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/GetConfigurationOptions";

    ptz_GetConfigurationOptions_req.ConfigurationToken = configurationToken;
    do
    {
        soap_call___tptz__GetConfigurationOptions(soap, soap_endpoint, soap_action, &ptz_GetConfigurationOptions_req, &ptz_GetConfigurationOptions_resp);
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

int ONVIF_PTZ_GotoHomePosition(char *username, char *password, char *ptzService, char *profileToken)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__GotoHomePosition ptz_GotoHomePosition_req;
    struct _tptz__GotoHomePositionResponse ptz_GotoHomePosition_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/GotoHomePosition";
    ptz_GotoHomePosition_req.ProfileToken = profileToken;
    ptz_GotoHomePosition_req.Speed = 0;
    printf("profileToken = %s\n", profileToken);
    do
    {
        soap_call___tptz__GotoHomePosition(soap, soap_endpoint, soap_action, &ptz_GotoHomePosition_req, &ptz_GotoHomePosition_resp);
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

int ONVIF_PTZ_ContinuousMove(char *username, char *password, char *ptzService, char *profileToken, TX_ONVIF_PTZ_Type type, float x, float y, float z)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__ContinuousMove ptz_ContinuousMove_req;
    struct _tptz__ContinuousMoveResponse ptz_ContinuousMove_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/ContinuousMove";
    ptz_ContinuousMove_req.ProfileToken = profileToken;
    struct tt__Vector2D vec2D;
    struct tt__Vector1D vec1D;
    struct tt__PTZSpeed velocity;
    
    if(type ==  tx_onvif_ptz_move)
    {
        vec2D.x =x;
        vec2D.y = y;
        vec2D.space = NULL;//= soap_strdup(soap, "http://www.onvif.org/ver10/schema");
        velocity.PanTilt = &vec2D;
        velocity.Zoom = NULL;
    }
    else if(type == tx_onvif_ptz_zoom)
    {
        vec1D.x = z;
        vec1D.space =  soap_strdup(soap, "http://www.onvif.org/ver10/tptz/ZoomSpaces/VelocityGenericSpace");
        velocity.PanTilt = NULL;
        velocity.Zoom = &vec1D;
    }
    else
    {
        printf("未知移动模式");
    }
    ptz_ContinuousMove_req.Velocity = &velocity;
    ptz_ContinuousMove_req.Timeout = 0;
    printf("profileToken = %s\n", profileToken);
    do
    {
        soap_call___tptz__ContinuousMove(soap, soap_endpoint, soap_action, &ptz_ContinuousMove_req, &ptz_ContinuousMove_resp);
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

int ONVIF_PTZ_Stop(char *username, char *password, char *ptzService, char* profileToken, TX_ONVIF_PTZ_Type type)
{
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tptz__Stop ptz_Stop_req;
    struct _tptz__StopResponse ptz_Stop_resp;

    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    strcpy(stUserInfo.username, username);
    strcpy(stUserInfo.password, password);
        
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    
    sprintf(soap_endpoint, "%s", ptzService);
    const char *soap_action = "http://www.onvif.org/ver10/ptz/wsdl/Stop";
    ptz_Stop_req.ProfileToken = profileToken;
    printf("profileToken = %s\n", profileToken);
    printf("type = %d\n", type);

    ptz_Stop_req.PanTilt = (enum xsd__boolean *)soap_malloc(soap, sizeof(enum xsd__boolean));
	ptz_Stop_req.Zoom = (enum xsd__boolean *)soap_malloc(soap, sizeof(enum xsd__boolean));
    if(type ==  tx_onvif_ptz_move)
    {
        *(ptz_Stop_req.PanTilt) = xsd__boolean__true_;
        *(ptz_Stop_req.Zoom) = xsd__boolean__false_;    
    }
    else if(type == tx_onvif_ptz_zoom)
    {
        *(ptz_Stop_req.PanTilt) = xsd__boolean__false_;
		*(ptz_Stop_req.Zoom) = xsd__boolean__true_;
    }
    else
    {
        ptz_Stop_req.PanTilt = 0;
        ptz_Stop_req.Zoom = 0;
    }
    do
    {
        soap_call___tptz__Stop(soap, soap_endpoint, soap_action, &ptz_Stop_req, &ptz_Stop_resp);
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
