import {compactDecode, compactEncode, hexToBytes} from '../src/codec'

test('compactDecode', () => {

    let buf = hexToBytes('0xed01')
    buf = compactDecode(buf)
    let number = buf[0]
    let len = buf[1]

    expect(number).toBe(123)
    expect(len).toBe(2)

})

test('compactEncode', () => {

    let number = 123
    let buf = compactEncode(number)

    expect(buf).toStrictEqual(hexToBytes('0xed01'))

})
