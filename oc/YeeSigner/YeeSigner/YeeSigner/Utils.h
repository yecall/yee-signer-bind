//
//  Utils.h
//  YeeSigner
//
//  Created by GB on 2020/8/2.
//  Copyright © 2020 io.yeeco. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Utils : NSObject

+ (NSData *) copyAndFreeVec: (unsigned int*) vecPointer error:(unsigned int* ) error;

@end
