#ifndef __BASE_64_H__
#define __BASE_64_H__

void base64_bits_to_64(unsigned char *out, const unsigned char *in, int inlen);
int base64_64_to_bits(char *out, const char *in);

#endif