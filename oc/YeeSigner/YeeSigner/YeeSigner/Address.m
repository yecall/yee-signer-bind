//
//  Address.m
//  YeeSigner
//
//  Created by GB on 2020/8/2.
//  Copyright © 2020 io.yeeco. All rights reserved.
//

#import "Address.h"
#import "libyee_signer.h"
#import "ErrorUtils.h"
#import "Utils.h"

@implementation Address

+ (NSString *) encode:(NSData* ) publicKey hrp:(NSString *) hrp error:(NSError **) error {
    
    unsigned int err = 0;
    
    NSData* hrpData = [hrp dataUsingEncoding:NSUTF8StringEncoding];
    unsigned int* vecPointer = yee_signer_address_encode(publicKey.bytes,  (unsigned int)publicKey.length, hrpData.bytes, (unsigned int)hrpData.length, &err);
    
    if(err > 0) {
        *error = [ErrorUtils error:err];
        return nil;
    }
    
    NSData *vec = [Utils copyAndFreeVec:vecPointer error:&err];
    
    if(err > 0) {
        *error = [ErrorUtils error:err];
        return nil;
    }
    
    NSString *address = [NSString stringWithUTF8String:[vec bytes]];
    
    return address;

}

+ (void) decode:(NSString* ) address publicKey:(NSData **) publicKey hrp:(NSString **) hrp error:(NSError **) error {
    
    unsigned int err = 0;
    
    unsigned int *publicKeyPointer = nil;
    unsigned int *hrpPointer = nil;
    
    NSData* addressData = [address dataUsingEncoding:NSUTF8StringEncoding];
    
    yee_signer_address_decode(addressData.bytes, (unsigned int)addressData.length, &publicKeyPointer, &hrpPointer, &err);
    
    if(err > 0) {
        *error = [ErrorUtils error:err];
        return;
    }
    
    NSData *publicKeyVec = [Utils copyAndFreeVec:publicKeyPointer error:&err];
    
    if(err > 0) {
        *error = [ErrorUtils error:err];
        return;
    }
    
    NSData *hrpVec = [Utils copyAndFreeVec:hrpPointer error:&err];
    
    if(err > 0) {
        *error = [ErrorUtils error:err];
        return;
    }
    
    NSString *hrpStr = [NSString stringWithUTF8String:[hrpVec bytes]];
    
    *publicKey = publicKeyVec;
    
    *hrp = hrpStr;
    
}

@end
