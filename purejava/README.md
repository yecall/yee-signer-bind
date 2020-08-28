# Yee signer java bind

Yee signer is a library to process schnorrkel signature and verification.

## Build

```
mvn package
```

## Usage

### Keypair generate
```java
KeyPair keyPair = KeyPair.generate();

byte[] publicKey = keyPair.getPublicKey();

byte[] secretKey = keyPair.getSecretKey();

```

### Sign and verify

```java
byte[] miniSecretKey = Hex.decodeHex("bc71cbf55c1b1cde2887126a27d0e42e596ac7d96eea9ea4b413e5b906eb630ecd859d888ab8f09aa0ff3b1075e0c1629cd491433e00dfb07e5a154312cc7d9b");
KeyPair keyPair = KeyPair.fromSecretKey(miniSecretKey);

byte[] publicKey = keyPair.getPublicKey();
assertEquals(Hex.encodeHexString(publicKey), "4ef0125fab173ceb93ce4c2a97e6824396240101b9c7220e3fd63e3a2282cf20");

byte[] secretKey = keyPair.getSecretKey();
assertEquals(Hex.encodeHexString(secretKey), "bc71cbf55c1b1cde2887126a27d0e42e596ac7d96eea9ea4b413e5b906eb630ecd859d888ab8f09aa0ff3b1075e0c1629cd491433e00dfb07e5a154312cc7d9b");
    
byte[] message = new byte[]{1, 2, 3};

byte[] signature = keyPair.sign(message);

Verifier verifier = Verifier.fromPublicKey(keyPair.getPublicKey());

verifier.verify(signature, message);

```

### Build transaction

```java

// params json
// dest:  address: 33 bytes, 0xFF + public key
// value: transfer value
String json = "{\"module\":4, \"method\":0, \"params\":{\"dest\":\"0xFF927b69286c0137e2ff66c6e561f721d2e6a2e9b92402d2eed7aebdca99005c70\",\"value\":1000}}";

// or dest: address: bech32 address
json = "{\"module\":4, \"method\":0, \"params\":{\"dest\":\"yee1jfakj2rvqym79lmxcmjkraep6tn296deyspd9mkh467u4xgqt3cqmtaf9v\",\"value\":1000}}";

Call call = Call.newCall(json);

// sender secret key: 64 bytes
byte[] secret_key = Hex.decodeHex("0b58d672927e01314d624fcb834a0f04b554f37640e0a4c342029a996ec1450bac8afb286e210d3afbfb8fd429129bd33329baaea6b919c92651c072c59d2408");

// sender nonce
long nonce = 0;

// era period: use 64
long period = 64;

// era current: the number of the best block
long current = 26491;

// era current hash: the hash of the best block
byte[] current_hash = Hex.decodeHex("c561eb19e88ce3728776794a9479e41f3ca4a56ffd01085ed4641bd608ecfe13");

Tx tx = Tx.buildTx(secret_key, nonce, period, current, current_hash, call);

// get the raw tx
byte[] encode = tx.encode();

```


### Address codec
```java

// encode
byte[] publicKey = Hex.decodeHex("0001020304050607080900010203040506070809000102030405060708090001");

String address = Address.encode(publicKey, "yee");

// decode
Pair<byte[], String> publicAndHrp = Address.decode(address);

byte[] publicKey = publicAndHrp.getValue0();
String hrp = publicAndHrp.getValue1();


```