package com.catalin.mymedic.feature.shared

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.LayoutErrorBinding

/**
 * A custom [FrameLayout] that acts as a wrapper on the child it contains, in order to display specific states like: empty, loading or error.
 *
 * USAGE: simply add it to your layout XML and add the view that has different states inside of it.
 *
 *
 * Notes and tips:
 *
 *  * The default state is LOADING by default. Other state values can also be accessed via XML.
 *  * The default show and hide animations are fade in / fade out.
 *  * Button clicks can easily be handled by setting click listeners in XML or by using data binding.
 *
 */

class StateLayout : FrameLayout {

    @LayoutRes
    var emptyLayoutId: Int = 0
    @LayoutRes
    private var loadingLayoutId: Int = 0
    @LayoutRes
    private var errorLayoutId: Int = 0

    private lateinit var state: State
    private lateinit var inflater: LayoutInflater

    private var stateChangeListener: ((State) -> Unit)? = null

    var animationEnabled = true
    private lateinit var showAnimation: Animation
    private lateinit var hideAnimation: Animation

    /**
     * The view that represents the content and not the states.
     */
    private lateinit var contentView: View

    /**
     * The state view. Can be null if the currently shown view is the content.
     */
    var stateView: View? = null
        private set

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        initialize(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        inflater = LayoutInflater.from(context)

        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.StateLayout)
        emptyLayoutId = styledAttributes.getResourceId(R.styleable.StateLayout_layout_empty, INVALID_LAYOUT_RESOURCE)
        loadingLayoutId = styledAttributes.getResourceId(R.styleable.StateLayout_layout_loading, INVALID_LAYOUT_RESOURCE)
        errorLayoutId = styledAttributes.getResourceId(R.styleable.StateLayout_layout_error, INVALID_LAYOUT_RESOURCE)

        // This flag is true by default, it can be change from xml
        animationEnabled = styledAttributes.getBoolean(R.styleable.StateLayout_disable_initial_animation, true)

        state = getStateFromValue(styledAttributes.getInt(R.styleable.StateLayout_state, State.LOADING.value))
        styledAttributes.recycle()

        initializeAnimations()
    }

    private fun initializeAnimations() {
        showAnimation = AlphaAnimation(0f, 1.0f)
        showAnimation.interpolator = AccelerateInterpolator()
        showAnimation.duration = (DEFAULT_ANIMATION_DURATION / 2).toLong()

        hideAnimation = AlphaAnimation(1.0f, 0f)
        hideAnimation.interpolator = DecelerateInterpolator()
        hideAnimation.duration = (DEFAULT_ANIMATION_DURATION / 2).toLong()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        contentView = getChildAt(0) ?: throw IllegalStateException("No content view available, your StateLayout is empty.")
        contentView.visibility = View.GONE

        updateStateView()
    }

    /**
     * Set the state change listener which will be invoked every time when the state has changed.
     */
    fun onStateChange(listener: (State) -> Unit) {
        stateChangeListener = listener
    }

    fun setEmpty(@LayoutRes emptyRes: Int) {
        emptyLayoutId = emptyRes
    }

    /**
     * Set the state value and update the UI.
     */
    @Throws(InvalidViewStateException::class)
    fun setState(state: Int) {
        if (state != this.state.value) {
            this.state = getStateFromValue(state)
            updateStateView()
        }
    }

    /**
     * Set the state value and update the UI.
     */
    @Throws(InvalidViewStateException::class)
    fun setState(state: State) {
        if (state != this.state) {
            this.state = state
            updateStateView()
        }
    }

    fun setOnErrorTryAgainListener(onErrorClick: () -> Unit) {
        if (state == State.ERROR) {
            stateView?.let { stateView ->
                LayoutErrorBinding.bind(stateView).retryButton.setOnClickListener { onErrorClick() }
            }
        }
    }

    /**
     * Hide / show content and state view according to the current state.
     *
     * @throws InvalidViewStateException if the state is invalid.
     */
    @Throws(InvalidViewStateException::class)
    private fun updateStateView() {
        stateView?.let { stateView ->
            if (animationEnabled) {
                hideAnimation.setAnimationListener(object : AnimationListenerAdapter {
                    override fun onAnimationEnd(animation: Animation) {
                        removeView(stateView)
                        handleStateChange()
                    }
                })
                stateView.startAnimation(hideAnimation)
            } else {
                removeView(stateView)
                handleStateChange()
            }
        } ?: handleStateChange()
    }

    private fun handleStateChange() {
        when (state) {
            State.NORMAL -> {
                if (animationEnabled) {
                    showAnimation.fillAfter = true
                    showAnimation.setAnimationListener(object : AnimationListenerAdapter {
                        override fun onAnimationStart(animation: Animation) {
                            contentView.visibility = View.VISIBLE
                        }
                    })
                    contentView.clearAnimation()
                    contentView.startAnimation(showAnimation)
                } else {
                    contentView.visibility = View.VISIBLE
                }
                stateView = null
            }
            State.EMPTY -> showStateView(emptyLayoutId)
            State.LOADING -> showStateView(loadingLayoutId)
            State.ERROR -> showStateView(errorLayoutId)
            State.INVALID -> throw InvalidViewStateException() // This can only happen if something is incorrectly set from XML
        }
        stateChangeListener?.invoke(state)
    }

    /**
     * Hides the content view and inflates the given layout as the new state view.
     *
     * @param layout the layout resource id that will be inflated as the new state.
     * @throws InvalidViewStateException when the layout resource id provided is not valid
     */
    private fun showStateView(@LayoutRes layout: Int) {
        if (layout == INVALID_LAYOUT_RESOURCE) {
            throw InvalidViewStateException("No layout provided for state: $state")
        } else {
            stateView = inflater.inflate(layout, this, false)
            stateView?.let { stateView ->
                (stateView.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER
                addView(stateView)

                if (animationEnabled) {
                    showAnimation.fillAfter = true
                    showAnimation.setAnimationListener(null)
                    stateView.startAnimation(showAnimation)
                }

                if (contentView.visibility == View.VISIBLE) {
                    if (animationEnabled) {
                        hideAnimation.setAnimationListener(object : AnimationListenerAdapter {
                            override fun onAnimationEnd(animation: Animation) {
                                contentView.visibility = View.GONE
                            }
                        })
                        contentView.startAnimation(hideAnimation)
                    } else {
                        contentView.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * Custom exception for handling invalid states in the [StateLayout]
     */
    class InvalidViewStateException @JvmOverloads constructor(
        message: String = "Invalid or unspecified view state, use one of the following states available in StateView: NORMAL, EMPTY, LOADING, NETWORK_ERROR or GENERAL_ERROR"
    ) :
        IllegalStateException(message)

    enum class State(val value: Int) {
        INVALID(-1), NORMAL(0), EMPTY(1), LOADING(2), ERROR(3)
    }

    companion object {
        private const val INVALID_LAYOUT_RESOURCE = -1
        private const val DEFAULT_ANIMATION_DURATION = 300

        fun getStateFromValue(value: Int) = when (value) {
            State.INVALID.value -> State.INVALID
            State.NORMAL.value -> State.NORMAL
            State.EMPTY.value -> State.EMPTY
            State.LOADING.value -> State.LOADING
            State.ERROR.value -> State.ERROR
            else -> State.INVALID
        }
    }

    interface AnimationListenerAdapter : Animation.AnimationListener {

        override fun onAnimationStart(animation: Animation) = Unit
        override fun onAnimationEnd(animation: Animation) = Unit
        override fun onAnimationRepeat(animation: Animation) = Unit

    }
}