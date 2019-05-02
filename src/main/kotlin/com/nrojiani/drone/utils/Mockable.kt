package com.nrojiani.drone.utils

/**
 * Indicates that the annotated class is made `open` (instead of the default, `final`)
 * and can be mocked. [More Info](https://trickyandroid.com/using-mockito-with-kotlin/).
 */
@Target(AnnotationTarget.CLASS)
annotation class Mockable
