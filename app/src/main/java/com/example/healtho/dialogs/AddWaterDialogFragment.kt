package com.example.healtho.dialogs

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.healtho.R
import com.example.healtho.databinding.FragmentAddWaterDialogBinding
import com.example.healtho.util.Objects
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddWaterDialogFragment (
    private val onAmountSelected: (Objects.WATER) -> Unit = {},
    private val onDismiss: () -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddWaterDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddWaterDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAllViews()
    }

    private fun setAllViews() {
        binding.quantity1.setOnClickListener {
            setWaterAndClose(Objects.WATER.ML_200)
        }
        binding.quantity2.setOnClickListener {
            setWaterAndClose(Objects.WATER.ML_400)
        }
        binding.quantity3.setOnClickListener {
            setWaterAndClose(Objects.WATER.ML_600)
        }
        binding.quantity4.setOnClickListener {
            setWaterAndClose(Objects.WATER.ML_800)
        }
        binding.quantity5.setOnClickListener {
            setWaterAndClose(Objects.WATER.ML_1000)
        }
    }

    private fun setWaterAndClose(water: Objects.WATER) {
        onAmountSelected(water)
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }
}