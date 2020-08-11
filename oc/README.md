# Yee signer Object-C bind

Yee signer is a library to process schnorrkel signature and verification.

## Build

### Requirements
```
rustup target add aarch64-apple-ios armv7-apple-ios armv7s-apple-ios x86_64-apple-ios i386-apple-ios
cargo install cargo-lipo
```

### Build
```
cd ../yee-signer
sh build.sh
```

## Usage

### Sign and verify

```objective-c

NSData* miniSecretKey = [NSData fromHex:@"579d7aa286b37b800b95fe41adabbf0c2a577caf2854baeca98f8fb242ff43ae"];
    
KeyPair* keyPair = [KeyPair fromMiniSecretKey:miniSecretKey error: &error];
    
NSData *publicKey = [keyPair publicKey:&error];
    
NSAssert([[publicKey toHex] isEqualToString:@"4ef0125fab173ceb93ce4c2a97e6824396240101b9c7220e3fd63e3a2282cf20"], @"");

NSData *secretKey = [keyPair secretKey:&error];
        
NSAssert([[secretKey toHex] isEqualToString:@"bc71cbf55c1b1cde2887126a27d0e42e596ac7d96eea9ea4b413e5b906eb630ecd859d888ab8f09aa0ff3b1075e0c1629cd491433e00dfb07e5a154312cc7d9b"], @"");
    

Verifier* verifier = [Verifier fromPublicKey:publicKey error:&error];
    
NSData *message = [NSData fromHex:@"010203"];

NSData *signature = [keyPair sign:message error:&error];
    
BOOL ok = [verifier verify:signature message:message error:&error];

NSAssert(ok, @"");

[keyPair free:&error];

[verifier free:&error];

```

### Build transaction

```objective-c
NSError* error = nil;
    
// params json
// dest:  address: 33 bytes, 0xFF + public key
// value: transfer value
NSString *json = @"{\"module\":4, \"method\":0, \"params\":{\"dest\":\"0xFF927b69286c0137e2ff66c6e561f721d2e6a2e9b92402d2eed7aebdca99005c70\",\"value\":1000}}";
Call* call = [Call buildCall:json error:&error];

// sender secret key: 64 bytes
NSData* secretKey = [NSData fromHex:@"0b58d672927e01314d624fcb834a0f04b554f37640e0a4c342029a996ec1450bac8afb286e210d3afbfb8fd429129bd33329baaea6b919c92651c072c59d2408"];

// sender nonce
u_long nonce = 0;

// era period: use 64
u_long period = 64;

// era current: the block number of the best block
u_long current = 26491;

// era current hash: the block hash of the best block
NSData* currentHash = [NSData fromHex:@"c561eb19e88ce3728776794a9479e41f3ca4a56ffd01085ed4641bd608ecfe13"];

Transaction* tx = [Transaction buildTx:secretKey nonce:nonce period:period current:current current_hash:currentHash call:call error:&error];

// get the raw tx
NSData* encode = [tx encode: &error];

[call free: &error];

[tx free: &error];

```

### Address codec

```objective-c

// encode
NSError* error = nil;
    
NSData* publicKey = [NSData fromHex:@"0001020304050607080900010203040506070809000102030405060708090001"];

NSString* hrp = @"yee";

NSString* address = [Address encode:publicKey hrp:hrp error:&error];

// decode
NSData* publicKey = nil;
NSString* hrp = nil;

[Address decode:address publicKey:&publicKey hrp:&hrp error:&error];


```

