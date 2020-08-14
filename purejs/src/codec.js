
function leToNumber(le) {
    let r = 0;
    let a = 1;
    le.forEach(x => { r += x * a; a *= 256; });
    return r;
}

function toLE(val, bytes) {
    let flip = false;
    if (val < 0) {
        val = -val - 1;
        flip = true;
    }

    let r = new Uint8Array(bytes);
    for (var o = 0; o < bytes; ++o) {
        r[o] = val % 256;
        if (flip) {
            r[o] = ~r[o] & 0xff;
        }
        val /= 256;
    }
    return r;
}

function hexToBytes(str) {
    if (!str) {
        return new Uint8Array();
    }
    var a = [];
    for (var i = str.startsWith('0x') ? 2 : 0, len = str.length; i < len; i += 2) {
        a.push(parseInt(str.substr(i, 2), 16));
    }

    return new Uint8Array(a);
}
function bytesToHex(uint8arr) {
    if (!uint8arr) {
        return '';
    }
    var hexStr = '';
    for (var i = 0; i < uint8arr.length; i++) {
        var hex = (uint8arr[i] & 0xff).toString(16);
        hex = (hex.length === 1) ? '0' + hex : hex;
        hexStr += hex;
    }

    return hexStr.toLowerCase();
}

function compactEncode(value) {
    if (value < 1 << 6) {
        return new Uint8Array([value << 2])
    } else if (value < 1 << 14) {
        return toLE((value << 2) + 1, 2)
    } else if (value < 1 << 30) {
        return toLE((value << 2) + 2, 4)
    } else {
        let bytes = 0;
        for (let v = value; v > 0; v = Math.floor(v / 256)) { ++bytes }
        return new Uint8Array([3 + ((bytes - 4) << 2), ...toLE(value, bytes)])
    }
}


function compactDecode(buff) {
    let res;
    let len;
    if (buff[0] % 4 == 0) {
        // one byte
        res = buff[0] >> 2;
        len = 1;
    } else if (buff[0] % 4 == 1) {
        res = leToNumber(buff.slice(0, 2)) >> 2;
        len = 2;
    } else if (buff[0] % 4 == 2) {
        res = leToNumber(buff.slice(0, 4)) >> 2;
        len = 4;
    } else {
        let n = (buff[0] >> 2) + 4;
        res = leToNumber(buff.slice(1, n + 1));
        len = 1 + n;
    }
    return [res, len]
}

export {
    hexToBytes,
    bytesToHex,
    compactEncode,
    compactDecode,
    leToNumber,
}

