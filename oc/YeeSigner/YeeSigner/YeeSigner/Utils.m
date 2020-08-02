//
//  Utils.m
//  YeeSigner
//
//  Created by GB on 2020/8/2.
//  Copyright © 2020 io.yeeco. All rights reserved.
//

#import "Utils.h"
#import "libyee_signer.h"
#import "ErrorUtils.h"

@implementation Utils

+ (NSData *) copyAndFreeVec: (unsigned int*) vecPointer error:(unsigned int* ) error{
    
    unsigned int err = 0;
    
    unsigned int vecLen = yee_signer_vec_len(vecPointer, &err);
    if(err > 0) {
        *error = err;
        yee_signer_vec_free(vecPointer, &err);
        return nil;
    }
    
    unsigned char buffer[vecLen];
    yee_signer_vec_copy(vecPointer, buffer, vecLen, &err);
    if(err > 0) {
        *error = err;
        yee_signer_vec_free(vecPointer, &err);
        return nil;
    }
    
    // free vec
    yee_signer_vec_free(vecPointer, &err);
    if(err > 0) {
        *error = err;
        return nil;
    }
    
    NSData* data = [NSData dataWithBytes:(const void *)buffer length:vecLen];
    return data;
    
}

@end
