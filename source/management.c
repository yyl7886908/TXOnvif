/************************************************
  Des:   This is the file for onvif devices manage
  Time: 2014-09-10
  Author: Yu Yun Long
 ************************************************/

#include "management.h"
#include "soapH.h"

#include "soapStub.h"
#include "stdsoap2.h"

#define USERNAME "admin"
#define PASSWORD "12345"



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

/* get capability */

int ONVIF_GetCapabilities(TX_Capability_Type txAbilityType, char *deviceService, LPTX_ONVIF_CAPABILITY_URI capabilityInfo)
{

#ifdef DEBUG
    printf(" [%s]-[%d] Search end!  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif

   int retval = 0;
    struct soap *soap = NULL;
    struct _tds__GetCapabilities capa_req;
    struct _tds__GetCapabilitiesResponse capa_resp;
        
    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    //\u6b63\u786e\u7684\u7528\u6237\u540d\u548c\u9519\u8bef\u7684\u5bc6\u7801
    strcpy(stUserInfo.username, USERNAME);
    strcpy(stUserInfo.password, PASSWORD);
        
    //\u6b64\u63a5\u53e3\u4e2d\u4f5c\u9a8c\u8bc1\u5904\u7406\uff0c \u5982\u679c\u4e0d\u9700\u8981\u9a8c\u8bc1\u7684\u8bdd\uff0cstUserInfo\u586b\u7a7a\u5373\u53ef
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    //\u6d77\u5eb7\u7684\u8bbe\u5907\uff0c\u56fa\u5b9aip\u8fde\u63a5\u8bbe\u5907\u83b7\u53d6\u80fd\u529b\u503c ,\u5b9e\u9645\u5f00\u53d1\u7684\u65f6\u5019\uff0c"172.18.14.22"\u5730\u5740\u4ee5\u53ca80\u7aef\u53e3\u53f7\u9700\u8981\u586b\u5199\u5728\u52a8\u6001\u641c\u7d22\u5230\u7684\u5177\u4f53\u4fe1\u606f
    /* sprintf(soap_endpoint, "http://%s:%d/onvif/device_service", DEVICE_IP, DEVICE_PORT);	 */
   sprintf(soap_endpoint, deviceService); 

    capa_req.Category = (enum tt__CapabilityCategory *)soap_malloc(soap, sizeof(int));
    capa_req.__sizeCategory = 1;
    *(capa_req.Category) = (enum tt__CapabilityCategory)tt__CapabilityCategory__All;
    //\u6b64\u53e5\u4e5f\u53ef\u4ee5\u4e0d\u8981\uff0c\u56e0\u4e3a\u5728\u63a5\u53e3soap_call___tds__GetCapabilities\u4e2d\u5224\u65ad\u4e86\uff0c\u5982\u679c\u6b64\u503c\u4e3aNULL,\u5219\u4f1a\u7ed9\u5b83\u8d4b\u503c
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetCapabilities";

    do
    {
        int ret = soap_call___tds__GetCapabilities(soap, soap_endpoint, soap_action, &capa_req, &capa_resp);
        printf("soap call caabilities, ret = %d======>\n", ret);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                break;
        }
        else   //\u83b7\u53d6\u53c2\u6570\u6210\u529f
        {
            // \u8d70\u5230\u8fd9\u91cc\u7684\u65f6\u5019\uff0c\u5df2\u7ecf\u5c31\u662f\u9a8c\u8bc1\u6210\u529f\u4e86\uff0c\u53ef\u4ee5\u83b7\u53d6\u5230\u53c2\u6570\u4e86\uff0c
            // \u5728\u5b9e\u9645\u5f00\u53d1\u7684\u65f6\u5019\uff0c\u53ef\u4ee5\u628acapa_resp\u7ed3\u6784\u4f53\u7684\u90a3\u4e9b\u9700\u8981\u7684\u503c\u5339\u914d\u5230\u81ea\u5df1\u7684\u79c1\u6709\u534f\u8bae\u4e2d\u53bb\uff0c\u7b80\u5355\u7684\u8d4b\u503c\u64cd\u4f5c\u5c31\u597d   

            if(capa_resp.Capabilities == NULL)
                {
                    printf("Get Capabilities fauled !  Capabilities == NULL");
                }
            else{

#ifdef DEBUG
                printf("[%s][%d] Get capabilities success !\n", __func__, __LINE__);
                printf("Capabilities->Media->XAddr = %s\n", capa_resp.Capabilities->Media->XAddr);
                printf("Capabilities->Imaging->XAddr = %s\n", capa_resp.Capabilities->Imaging->XAddr);
                printf("Capabilities->Events->XAddr = %s\n", capa_resp.Capabilities->Events->XAddr);
                printf("Capabilities->PTX->XAddr = %s\n", capa_resp.Capabilities->PTZ->XAddr);
             
#endif
                
                /* 给capability结构体赋值 */
                strcpy(capabilityInfo->analytics, capa_resp.Capabilities->Analytics->XAddr);
                strcpy(capabilityInfo->device, capa_resp.Capabilities->Device->XAddr);
                strcpy(capabilityInfo->events, capa_resp.Capabilities->Events->XAddr);
                strcpy(capabilityInfo->imaging,  capa_resp.Capabilities->Imaging->XAddr);
                strcpy(capabilityInfo->media,  capa_resp.Capabilities->Media->XAddr);
                strcpy(capabilityInfo->ptz, capa_resp.Capabilities->PTZ->XAddr);
            }
             
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
}

/* get device info */
int ONVIF_GetDeviceInfo(char *deviceService, LPTX_ONVIF_DEVICE_INFO deviceInfo)
{
   
#ifdef DEBUG
    printf(" [%s]-[%d] Search end!  deviceService = %s \n", __func__, __LINE__, deviceService);
#endif
    int retval = 0;
    struct soap *soap = NULL;
    
    struct _tds__GetDeviceInformation devinfo_req;
    struct _tds__GetDeviceInformationResponse devinfo_resq;

        
    struct SOAP_ENV__Header header;

    UserInfo_S stUserInfo;
    memset(&stUserInfo, 0, sizeof(UserInfo_S));
 
    //\u6b63\u786e\u7684\u7528\u6237\u540d\u548c\u9519\u8bef\u7684\u5bc6\u7801
    strcpy(stUserInfo.username, USERNAME);
    strcpy(stUserInfo.password, PASSWORD);
        
    //\u6b64\u63a5\u53e3\u4e2d\u4f5c\u9a8c\u8bc1\u5904\u7406\uff0c \u5982\u679c\u4e0d\u9700\u8981\u9a8c\u8bc1\u7684\u8bdd\uff0cstUserInfo\u586b\u7a7a\u5373\u53ef
    memset(&header,0,sizeof(header));
    soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo);
    char *soap_endpoint = (char *)malloc(256);
    memset(soap_endpoint, '\0', 256);
    //\u6d77\u5eb7\u7684\u8bbe\u5907\uff0c\u56fa\u5b9aip\u8fde\u63a5\u8bbe\u5907\u83b7\u53d6\u80fd\u529b\u503c ,\u5b9e\u9645\u5f00\u53d1\u7684\u65f6\u5019\uff0c"172.18.14.22"\u5730\u5740\u4ee5\u53ca80\u7aef\u53e3\u53f7\u9700\u8981\u586b\u5199\u5728\u52a8\u6001\u641c\u7d22\u5230\u7684\u5177\u4f53\u4fe1\u606f
    sprintf(soap_endpoint, deviceService);	

    //\u6b64\u53e5\u4e5f\u53ef\u4ee5\u4e0d\u8981\uff0c\u56e0\u4e3a\u5728\u63a5\u53e3soap_call___tds__GetCapabilities\u4e2d\u5224\u65ad\u4e86\uff0c\u5982\u679c\u6b64\u503c\u4e3aNULL,\u5219\u4f1a\u7ed9\u5b83\u8d4b\u503c
    const char *soap_action = "http://www.onvif.org/ver10/device/wsdl/GetDeviceInformation";

    do
    {
        soap_call___tds__GetDeviceInformation(soap, soap_endpoint, soap_action, &devinfo_req, &devinfo_resq);
        if (soap->error)
        {
                printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap));
                retval = soap->error;
                return retval;
        }
        else   //\u83b7\u53d6\u53c2\u6570\u6210\u529f
        {         
              printf("[%s][%d]  GetDeviceInformation success !\n", __func__, __LINE__);
              /* 给deviceInfo结构体赋值 */
              /* sprintf(deviceInfo.manufacturer, ); */
#ifdef DEBUG
              printf("[%s][%d]  GetDeviceInformation manufacturer = %s !\n", __func__, __LINE__, devinfo_resq.Manufacturer);
              printf("[%s][%d]  GetDeviceInformation SerialNumber = %s !\n", __func__, __LINE__, devinfo_resq.SerialNumber);
              printf("[%s][%d]  GetDeviceInformation FirmwareVersion = %s !\n", __func__, __LINE__, devinfo_resq.FirmwareVersion);
              printf("[%s][%d]  GetDeviceInformation Model  = %s !\n", __func__, __LINE__, devinfo_resq.Model);
#endif
              
              strcpy(deviceInfo->manufacturer, devinfo_resq.Manufacturer);
              strcpy(deviceInfo->model, devinfo_resq.Model);
              strcpy(deviceInfo->firmwareVersion, devinfo_resq.FirmwareVersion);
              strcpy(deviceInfo->serialNumber, devinfo_resq.SerialNumber );
              strcpy(deviceInfo->hardwareId, devinfo_resq.HardwareId );
              
        }
    }while(0);

    free(soap_endpoint);
    soap_endpoint = NULL;
    soap_destroy(soap);
    return retval;
    
}

int ONVIF_DeviceReboot(char* inBuffer)
{
#ifdef DEBUG
    printf(" [%s]-[%d] Search end!  inBuffer = %s \n", __func__, __LINE__,  inBuffer);
#endif
    
    return 0;
}


/* int ONVIF_GetSysDataTime(char *deviceService, STime &time) */
/* { */
/*     int retval = 0; */
/*     struct soap *soap = NULL; */
/*     struct SOAP_ENV__Header header;	 */

/*     struct _tds__GetSystemDateAndTime sdat_req; */
/*     struct _tds__GetSystemDateAndTimeResponse sdat_resp; */

/*     UserInfo_S stUserInfo; */
/*     memset(&stUserInfo, 0, sizeof(UserInfo_S)); */
 
/*     //\u6b63\u786e\u7684\u7528\u6237\u540d\u548c\u9519\u8bef\u7684\u5bc6\u7801 */
/*     strcpy(stUserInfo.username, USERNAME); */
/*     strcpy(stUserInfo.password, PASSWORD); */

/*     soap = ONVIF_Initsoap(&header, NULL, NULL, 5, &stUserInfo); */
/*     char *soap_endpoint = (char *)malloc(256); */
/*     memset(soap_endpoint, '\0', 256); */
/*     //\u6d77\u5eb7\u7684\u8bbe\u5907\uff0c\u56fa\u5b9aip\u8fde\u63a5\u8bbe\u5907\u83b7\u53d6\u80fd\u529b\u503c ,\u5b9e\u9645\u5f00\u53d1\u7684\u65f6\u5019\uff0c"172.18.14.22"\u5730\u5740\u4ee5\u53ca80\u7aef\u53e3\u53f7\u9700\u8981\u586b\u5199\u5728\u52a8\u6001\u641c\u7d22\u5230\u7684\u5177\u4f53\u4fe1\u606f */
/*     sprintf(soap_endpoint, "http://%s:%d/onvif/device_service", DEVICE_IP, DEVICE_PORT);	 */

/*     const char *soap_action ="http://www.onvif.org/ver10/device/wsdl/GetSystemDateAndTime"; */

/*     do */
/*     {	 */
/*         soap_call___tds__GetSystemDateAndTime(soap,soap_endpoint,soap_action,&sdat_req,&sdat_resp); */

/*         if (soap->error) */
/*         { */
/*                 printf("[%s][%d]--->>> soap error: %d, %s, %s\n", __func__, __LINE__, soap->error, *soap_faultcode(soap), *soap_faultstring(soap)); */
/*                 retval = soap->error; */
/*                 break; */
/*         } */
/*         else   //\u83b7\u53d6\u53c2\u6570\u6210\u529f */
/*         {    */
/*               printf("[%s][%d] Get system date and time success !\n", __func__, __LINE__); */
/*               printf("local time is %04d-%02d-%02d %02d:%02d:%02d\n",sdat_resp.SystemDateAndTime->LocalDateTime->Date->Year, */
/*                sdat_resp.SystemDateAndTime->LocalDateTime->Date->Month, */
/*                sdat_resp.SystemDateAndTime->LocalDateTime->Date->Day, */
/*                sdat_resp.SystemDateAndTime->LocalDateTime->Time->Hour, */
/*                sdat_resp.SystemDateAndTime->LocalDateTime->Time->Minute, */
/*                sdat_resp.SystemDateAndTime->LocalDateTime->Time->Second); */
/*         }         */
/*     }while(0);  */

/*     free(soap_endpoint); */
/*     soap_endpoint = NULL; */
/*     soap_destroy(soap); */
/*     return retval;     */
/* } */
