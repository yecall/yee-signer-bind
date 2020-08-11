import {bytesToHex, compactDecode} from "./codec"
import bech32 from 'bech32'

var Network = {
    Testnet: 'testnet',
    Mainnet: 'mainnet',
}

function address(buf, network) {

    let hrp = network == Network.Testnet ? 'tyee' : 'yee'

    buf = buf.slice(1)
    buf = Buffer.from(buf)
    buf = bech32.toWords(buf)
    buf = bech32.encode(hrp, buf)
    return buf
}

function balancesTransferParamsDecode(buf, network) {

    if (buf[0] != 0xFF) {
        return ''
    }
    let dest = buf.slice(0, 33)
    buf = buf.slice(33)

    let value = compactDecode(buf)
    value = value[0]

    return {
        dest: address(dest, network),
        value: value,
    }
}

function relayTransferParamsDecode(buf, network) {

    let relayType = buf[0]
    relayType = relayType == 0 ? 'Balance' : ( relayType ==1 ? 'Assets' : '' )
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

function txDecode(raw, network) {

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
            sender: address(sender, network),
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
        params = balancesTransferParamsDecode(raw, network)
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
    Network,
}
