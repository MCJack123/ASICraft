# ASICraft
A mod for CC:Tweaked that adds acceleration cards for various encoding, decoding, and transformation routines.

## Gameplay
ASICraft provides algorithms through *acceleration cards*, which are crafted with various parts, and inserted into an *expansion bus*, which functions as a holder and bridge to ComputerCraft.

### Creating cards
The first step in making an acceleration card is acquiring an *algorithm*. Algorithms can be drafted using ???, or found in stronghold library chests.

Once you have an algorithm, you need a *microchip designer* to create a *microchip* for the card. Inside the designer, place the algorithm in the center slot, and one iron ingot, gold ingot, redstone, and stone block in the slots on the left. A microchip with the algorithm provided will be created, which uses up the items on the left, but keeps the algorithm intact.

With the microchip, you can use the *card builder* to create an acceleration card using a blank circuit board, which is craftable in the crafting table.

### Configuring
To use the acceleration cards, you need an expansion bus. Three tiers of expansion bus are available with 1, 3, and 6 slots, respectively.

Simply drop the acceleration card(s) into any open slot in the expansion bus. Then make sure the bus is connected to the computer, either by being next to it, or through a wired modem (which must be right-clicked before it'll connect).

Acceleration cards can have their outputs automatically sent as input to the next card. To do this, click the small square on the arrow connecting two cards together. When the arrow is connected with no gap, the output will be forwarded to the next card.

### Programming
All cards have a common interface structure, with a set of properties and an input stream.

To start, wrap the expansion bus peripheral using `peripheral.wrap` or `peripheral.find("asicraft:expansion_bus")`. Once wrapped, the available card types can be queried with `bus.getCardType(slot)`, where `slot` is the slot to check starting at 1.

Properties for a card can be listed using `bus.getAvailableProperties(slot)`. This function returns a list of properties, which each consist of a list with the name and type of the property. Use `bus.getProperty(slot, name)` to get the current value of a property, and `bus.setProperty(slot, name, value)` to set a property. More information on each algorithm's properties is available below.

Once the card's properties are configured, you can start streaming data to the card. Call `bus.input(slot, data)` to send data to the card, either all at once or in chunks. Once all of the data has been sent, use `bus.finish(slot)` to tell the card to close the data stream, output its result, and reset to prepare for the next data stream. You can also use the `bus.process(slot, data)` function to send data, finish, and wait for a result all in one call.

After calling `finish` on a card, it will compute the result, and send it back using an `asicraft.result` event. This event has four parameters: the network name of the bus, the slot number of the output card (which may differ from the input if they are chained), whether the computation succeeded, and either the result data on success, or an error message on failure.

Cards cannot accept more input while processing, so wait for the `asicraft.result` event before sending more data to the card. In addition, most cards can't change parameters while input is in progress, so avoid calling `setProperty` after calling `input` before `finish`.

Some cards support partial result reporting, which sends processed data back in chunks after `input` without calling `finish`. To enable, set the `partial` property to `true`. Once set, each `input` call will be followed by an `asicraft.partial_result` event, which contains the peripheral name, slot number, and partial data result.

## Algorithms
### AES Encryptor
* Input: plain text (string)
* Output: encrypted data (string)
* Properties:

| Name         | Type    | Description                                                                   |
|--------------|---------|:------------------------------------------------------------------------------|
| `partial`    | boolean | Whether to send partial results                                               |
| `key`        | string  | The encryption key (16, 24, or 32 bytes) - use the PBKDF2 card to derive this |
| `iv`         | string  | The initialization vector (IV) (16 bytes)                                     |
| `cipherMode` | string  | The cipher mode to use (ECB, CBC, CFB, OFB, CTR, GCM)                         |

### AES Decryptor
* Input: encrypted data (string)
* Output: plain text (string)
* Properties:

| Name         | Type    | Description                                                                   |
|--------------|---------|:------------------------------------------------------------------------------|
| `partial`    | boolean | Whether to send partial results                                               |
| `key`        | string  | The encryption key (16, 24, or 32 bytes) - use the PBKDF2 card to derive this |
| `iv`         | string  | The initialization vector (IV) (16 bytes)                                     |
| `cipherMode` | string  | The cipher mode to use (ECB, CBC, CFB, OFB, CTR, GCM)                         |

### RSA Encryptor
* Input: plain text (string)
* Output: encrypted data (string)
* Properties:

| Name         | Type     | Description                                                                                  |
|--------------|----------|:---------------------------------------------------------------------------------------------|
| `partial`    | boolean  | Whether to send partial results                                                              |
| `key`        | string   | The key to encrypt with, in PKCS#1 format (DER SEQUENCE with modulus, exponent INTEGER pair) |
| `isPublic`   | boolean  | Whether the key provided is a public key                                                     |

(PKCS#1 format was chosen because the same structure is used in both X.509 public key certificates and PKCS#8 private key files to hold the key.)

### RSA Decryptor
* Input: encrypted data (string)
* Output: plain text (string)
* Properties:

| Name         | Type     | Description                                                                                  |
|--------------|----------|:---------------------------------------------------------------------------------------------|
| `partial`    | boolean  | Whether to send partial results                                                              |
| `key`        | string   | The key to decrypt with, in PKCS#1 format (DER SEQUENCE with modulus, exponent INTEGER pair) |
| `isPublic`   | boolean  | Whether the key provided is a public key                                                     |

### RSA Key Generator
* Input: none (ignored)
* Output: Table with `public` and `private` members, containing PKCS#1 keys
* Properties:

| Name   | Type   | Description                                                      |
|--------|--------|:-----------------------------------------------------------------|
| `size` | number | The size in bits of the key to generate (1024, 2048, 3072, 4096) |

### ChaCha20 Encryptor
* Input: plain text (string)
* Output: encrypted data (string)
* Properties:

| Name      | Type    | Description                        |
|-----------|---------|:-----------------------------------|
| `partial` | boolean | Whether to send partial results    |
| `key`     | string  | The key to encrypt with (32 bytes) |
| `nonce`   | string  | A random nonce (12 bytes)          |

### ChaCha20 Decryptor
* Input: encrypted data (string)
* Output: plain text (string)
* Properties:

| Name      | Type    | Description                        |
|-----------|---------|:-----------------------------------|
| `partial` | boolean | Whether to send partial results    |
| `key`     | string  | The key to decrypt with (32 bytes) |
| `nonce`   | string  | A random nonce (12 bytes)          |

### PBKDF2 Password Hasher
* Input: password (string)
* Output: encryption key (string, `bits`/8 bytes)
* Properties:

| Name     | Type   | Description                                                    |
|----------|--------|:---------------------------------------------------------------|
| `rounds` | number | The number of iterations to run (default 200,000)              |
| `bits`   | number | The number of bits in the resulting key                        |
| `salt`   | string | A 16-byte random salt string, or `""` to generate a random one |

### SHA-256 Hasher
* Input: data (string)
* Output: hash (string, 32 bytes)
* No properties

### SHA3-256 Hasher
* Input: data (string)
* Output: hash (string, 32 bytes)
* No properties

## License
ASICraft is licensed under the GPLv2 license, or any later version at your preference.
