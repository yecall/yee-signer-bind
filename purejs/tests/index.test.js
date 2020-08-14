import {txDecode, shard_num_for_account, Network} from '../src/index'
import {hexToBytes} from '../src/codec'

test('txDecode', () => {

    let raw = hexToBytes("0x390281ff44a5fbc7fff3cb26358e6070fbe7e171f978a5bf6de3dcc7ba359bb783916d446ee29d8f6a375c31ad49e990ef55901dc1211424bc2bf00345aa8846aeef733ced73a13c88e74c2c5d66ba6fe4d13eaadfc27e03a5b9c293e6123ef8d8cad80600d5020400ff36b116bcdeff6bf63539cea3cafdd90bb53d6df043b2ef791d234c92ca5de8040700d0ed902e")

    let tx = txDecode(raw, Network.Mainnet, 4)

    let expected = {
        signature:
            {
                sender: "0xff44a5fbc7fff3cb26358e6070fbe7e171f978a5bf6de3dcc7ba359bb783916d44",
                sender_address: "yee1gjjlh3ll709jvdvwvpc0helpw8uh3fdldh3ae3a6xkdm0qu3d4zqg2d5d3",
                sender_shard_num: 0,
                signature:
                    '0x6ee29d8f6a375c31ad49e990ef55901dc1211424bc2bf00345aa8846aeef733ced73a13c88e74c2c5d66ba6fe4d13eaadfc27e03a5b9c293e6123ef8d8cad806',
                nonce: 0
            },
        call:
            {
                module: 4,
                method: 0,
                params:
                    {
                        dest: "0xff36b116bcdeff6bf63539cea3cafdd90bb53d6df043b2ef791d234c92ca5de804",
                        dest_address: "yee1x6c3d0x7la4lvdfee63u4lwepw6n6m0sgwew77gaydxf9jjaaqzqzzu8dj",
                        dest_shard_num: 0,
                        value: 200000000000
                    }
            }
    }
    expect(tx).toStrictEqual(expected)

})

test('txDecodeWithoutPrefix', () => {

    let raw = hexToBytes("0x81ff44a5fbc7fff3cb26358e6070fbe7e171f978a5bf6de3dcc7ba359bb783916d446ee29d8f6a375c31ad49e990ef55901dc1211424bc2bf00345aa8846aeef733ced73a13c88e74c2c5d66ba6fe4d13eaadfc27e03a5b9c293e6123ef8d8cad80600d5020400ff36b116bcdeff6bf63539cea3cafdd90bb53d6df043b2ef791d234c92ca5de8040700d0ed902e")

    let tx = txDecode(raw, Network.Mainnet, 4)

    let expected = {
        signature:
            {
                sender: "0xff44a5fbc7fff3cb26358e6070fbe7e171f978a5bf6de3dcc7ba359bb783916d44",
                sender_address: "yee1gjjlh3ll709jvdvwvpc0helpw8uh3fdldh3ae3a6xkdm0qu3d4zqg2d5d3",
                sender_shard_num: 0,
                signature:
                    '0x6ee29d8f6a375c31ad49e990ef55901dc1211424bc2bf00345aa8846aeef733ced73a13c88e74c2c5d66ba6fe4d13eaadfc27e03a5b9c293e6123ef8d8cad806',
                nonce: 0
            },
        call:
            {
                module: 4,
                method: 0,
                params:
                    {
                        dest: "0xff36b116bcdeff6bf63539cea3cafdd90bb53d6df043b2ef791d234c92ca5de804",
                        dest_address: "yee1x6c3d0x7la4lvdfee63u4lwepw6n6m0sgwew77gaydxf9jjaaqzqzzu8dj",
                        dest_shard_num: 0,
                        value: 200000000000
                    }
            }
    }

    expect(tx).toStrictEqual(expected)

})

test('txDecodeRelay', () => {

    let raw = hexToBytes("0x6103010900004102390281ff36b116bcdeff6bf63539cea3cafdd90bb53d6df043b2ef791d234c92ca5de80480ba2ca34dfe11d120a8c610534887312c79e5c247da9b4f31ea7495a4376f6a9512d3f0b771c923142c46dc33ef6f924f86b8f7bcd1749eb2e15aa388bddb090495000400ffc49bc1483a1669d65b19274445cb86604b7eca1d8e8d062269c8c6796a45b6250700e40b54022906c34449ad91dfa044c4d314b1b22762189bb3ad4a8577a9050e90e443f3550afcf78eb90a94e881b488b8c3a81905d0424e2c55834a819164c7afba2594f43318")

    let tx = txDecode(raw, Network.Mainnet, 4)

    let expected = {
        signature: null,
        call:
            {
                module: 9,
                method: 0,
                params:
                    {
                        relayType: 'Balance',
                        tx:
                            '0x390281ff36b116bcdeff6bf63539cea3cafdd90bb53d6df043b2ef791d234c92ca5de80480ba2ca34dfe11d120a8c610534887312c79e5c247da9b4f31ea7495a4376f6a9512d3f0b771c923142c46dc33ef6f924f86b8f7bcd1749eb2e15aa388bddb090495000400ffc49bc1483a1669d65b19274445cb86604b7eca1d8e8d062269c8c6796a45b6250700e40b5402',
                        number: 394,
                        hash:
                            '0xc34449ad91dfa044c4d314b1b22762189bb3ad4a8577a9050e90e443f3550afc',
                        parent:
                            '0xf78eb90a94e881b488b8c3a81905d0424e2c55834a819164c7afba2594f43318'
                    }
            }
    }

    expect(tx).toStrictEqual(expected)

})

test('shardNum4', () => {

    let account = hexToBytes("0xFFd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")

    let shard_num = shard_num_for_account(account, 4);

    expect(shard_num).toStrictEqual(1)

})

test('shardNum8', () => {

    let account = hexToBytes("0xFFd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")

    let shard_num = shard_num_for_account(account, 8);

    expect(shard_num).toStrictEqual(5)

})

test('shardNum16', () => {

    let account = hexToBytes("0xFFd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")

    let shard_num = shard_num_for_account(account, 16);

    expect(shard_num).toStrictEqual(13)

})

