import {bytesToHex, compactDecode, leToNumber} from "./codec"
import bech32 from 'bech32'

var Network = {
    Testnet: 'testnet',
    Mainnet: 'mainnet',
}

function address_for_account(buf, network) {

    let hrp = network == Network.Testnet ? 'tyee' : 'yee'

    buf = buf.slice(1)
    buf = Buffer.from(buf)
    buf = bech32.toWords(buf)
    buf = bech32.encode(hrp, buf)
    return buf
}

function shard_num_for_account(buf, shard_count) {
    buf = buf.slice(1)
    let len = buf.length;
    if (len < 2) {
        return ''
    }
    if (shard_count == 0) {
        return ''
    }
    let digits = log2(shard_count)
    if (pow2(digits) != shard_count) {
        return ''
    }

    let a = leToNumber([buf[len-1], buf[len-2]])
    let mask = ~(~0 << digits)
    let result = a & mask
    return result
}

function log2(n) {
    let s = n
    let i = 0
    while (s > 0) {
        s = s >> 1
        i = i + 1
    }
    return i - 1
}

function pow2(n) {
    return 1 << n
}

function balancesTransferParamsDecode(buf, network, shard_count) {

    if (buf[0] != 0xFF) {
        return ''
    }
    let dest = buf.slice(0, 33)
    buf = buf.slice(33)

    let value = compactDecode(buf)
    value = value[0]

    return {
        dest: '0x' + bytesToHex(dest),
        dest_address: address_for_account(dest, network),
        dest_shard_num: shard_num_for_account(dest, shard_count),
        value: value,
    }
}

function relayTransferParamsDecode(buf, network) {

    let relayType = buf[0]
    relayType = relayType == 0 ? 'Balance' : (relayType == 1 ? 'Assets' : '')
    if (!relayType) {
        return ''
    }
    buf = buf.slice(1)

    let txPrefix = compactDecode(buf)
    let txLen = txPrefix[0]
    let txPrefixLen = txPrefix[1]
    buf = buf.slice(txPrefixLen)

    let tx = buf.slice(0, txLen)
    buf = buf.slice(txLen)

    let number = compactDecode(buf)
    let numberLen = number[1]
    number = number[0]
    buf = buf.slice(numberLen)

    let hash = buf.slice(0, 32)
    buf = buf.slice(32)

    let parent = buf.slice(0, 32)

    return {
        relayType,
        tx: '0x' + bytesToHex(tx),
        number: number,
        hash: '0x' + bytesToHex(hash),
        parent: '0x' + bytesToHex(parent),
    }
}

function txDecode(raw, network, shard_count) {

    if (!raw) {
        return ''
    }

    let prefix = compactDecode(raw)
    let rawLen = prefix[0]
    let prefixLen = prefix[1]

    if (rawLen + prefixLen == raw.length) {
        raw = raw.slice(prefixLen)
    }

    let version = raw[0]
    let is_signed = (version & 0b1000_0000) != 0
    version = version & 0b0111_1111
    if (version != 1) {
        return ''
    }
    raw = raw.slice(1)

    let sender
    let sig
    let nonce
    let signature = null

    if (is_signed) {
        if (raw[0] != 0xFF) {
            return ''
        }
        sender = raw.slice(0, 33)
        raw = raw.slice(33)

        sig = raw.slice(0, 64)
        raw = raw.slice(64)

        nonce = compactDecode(raw)
        let nonceLen = nonce[1]
        nonce = nonce[0]
        raw = raw.slice(nonceLen)

        // omit era
        raw = raw.slice(2)

        signature = {
            sender: '0x' + bytesToHex(sender),
            sender_address: address_for_account(sender, network),
            sender_shard_num: shard_num_for_account(sender, shard_count),
            signature: '0x' + bytesToHex(sig),
            nonce: nonce,
        }
    }

    let module = raw[0]
    raw = raw.slice(1)

    let method = raw[0]
    raw = raw.slice(1)

    let params

    if (module == 4 && method == 0) {
        params = balancesTransferParamsDecode(raw, network, shard_count)
    } else if (module == 9 && method == 0) {
        params = relayTransferParamsDecode(raw, network)
    } else {
        return ''
    }

    let tx = {
        signature,
        call: {
            module,
            method,
            params,
        }
    }
    return tx

}

export {
    txDecode,
    shard_num_for_account,
    Network,
}
