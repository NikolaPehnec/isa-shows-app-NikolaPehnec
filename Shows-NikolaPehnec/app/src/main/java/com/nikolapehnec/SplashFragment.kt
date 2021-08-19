package com.nikolapehnec

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.nikolapehnec.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val splashBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return splashBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(splashBinding.triangle) {
            animate().translationY(900f)
                .setInterpolator(BounceInterpolator())
                .setDuration(1500)
                .withEndAction {
                    //provjere zbog zatvaranja aplikacije prije dovrsetka animacije
                    if(_binding!=null) splashBinding.title.isVisible = true
                    postDelayed({
                        try {
                            findNavController().navigate(R.id.actionToLogin)
                        } catch (e: IllegalStateException) {

                        }
                    }, 2500)
                }
                .start()

        }

        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            splashBinding.title,
            PropertyValuesHolder.ofFloat("scaleX", 1.5f),
            PropertyValuesHolder.ofFloat("scaleY", 1.5f),
        )
        scaleUp.duration = 2000

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            splashBinding.title,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f),
        )
        scaleDown.duration = 500

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
            scaleUp,
            scaleDown
        )
        animatorSet.start()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}