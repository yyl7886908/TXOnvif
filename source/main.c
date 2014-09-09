#include "discovery.h"

int main()
{
    printf("start main---------------->\n");
    int ret = ONVIF_Discoverty() ;
    printf("ret = %d\n", ret);
    
}
