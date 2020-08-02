//
//  Address.h
//  YeeSigner
//
//  Created by GB on 2020/8/2.
//  Copyright © 2020 io.yeeco. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Address : NSObject

+ (NSString *) encode:(NSData* ) publicKey hrp:(NSString *) hrp error:(NSError **) error;

+ (void) decode:(NSString* ) address publicKey:(NSData **) publicKey hrp:(NSString **) hrp error:(NSError **) error;

@end
