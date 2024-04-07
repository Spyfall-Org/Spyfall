package com.dangerfield.libraries.navigation

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes

class NavAnimBuilder {
    /**
     * The custom Animation or Animator resource for the enter animation.
     *
     * Note: Animator resources are not supported for navigating to a new Activity
     */
    @AnimRes
    @AnimatorRes
    public var enter: Int = -1

    /**
     * The custom Animation or Animator resource for the exit animation.
     *
     * Note: Animator resources are not supported for navigating to a new Activity
     */
    @AnimRes
    @AnimatorRes
    public var exit: Int = -1

    /**
     * The custom Animation or Animator resource for the enter animation
     * when popping off the back stack.
     *
     * Note: Animator resources are not supported for navigating to a new Activity
     */
    @AnimRes
    @AnimatorRes
    public var popEnter: Int = -1

    /**
     * The custom Animation or Animator resource for the exit animation
     * when popping off the back stack.
     *
     * Note: Animator resources are not supported for navigating to a new Activity
     */
    @AnimRes
    @AnimatorRes
    public var popExit: Int = -1
}