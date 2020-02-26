# What is this

This is the Sandbox-customized implementation of XS2A protocol. You can follow this example to customize XS2A protocol.

## How it works

We import remote resources from pristine xs2a protocol and perform word based bean names replacement based on
[context.properties](src/main/resources/context.properties). In particular, only `xs2a-list-transactions` flow is
changed to use special customized version for Sandbox. As the result, this module provides only one flow - 
`xs2a-sandbox-list-transactions`, but Flowable will detect original flows as well from parent JAR, 
even if it is not included directly.

## Details
This module is the XS2A protocol customization for Adorsys Dynamic Sandbox. In particular, to acquire transaction list
(on pristine database) Sandbox requires that get account list was called. 
