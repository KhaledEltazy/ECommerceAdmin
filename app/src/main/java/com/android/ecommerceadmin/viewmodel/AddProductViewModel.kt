package com.android.ecommerceadmin.viewmodel



import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.Product
import com.android.ecommerceadmin.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    val firestore: FirebaseFirestore
) : ViewModel() {


    private val _category = MutableLiveData<String>()
    val category = _category
    fun setCategory(category: String) {
        _category.value = category
    }

    private val _saveProduct = MutableStateFlow<Resource<Product>>(Resource.Undefined())
    val saveProduct = _saveProduct.asSharedFlow()

    private val _editProduct = MutableStateFlow<Resource<Product>>(Resource.Undefined())
    val editProduct = _editProduct.asSharedFlow()

    fun saveProduct(productName: String, category: String, price : Float,
                    offer:Float? = null, description: String? = null,
                    color : List<Int>?= null, sizes: List<String>? = null,
                    images: List<String>, stock : Int){
        //create product Object
        val product = Product(
            UUID.randomUUID().toString(),
            productName,
            category,
            price,
            offer,
            description,
            color,
            sizes,
            images,
            stock
        )

        //save product to FireStore
        viewModelScope.launch {
            _saveProduct.emit(Resource.Loading())
        }
        firestore.collection("products").add(product).addOnSuccessListener {
            viewModelScope.launch {
                _saveProduct.emit(Resource.Success(product))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _saveProduct.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    fun editProduct(id: String, productName: String, category: String, price: Float,
                    offer: Float? = null, description: String? = null,
                    color: List<Int>? = null, sizes: List<String>? = null,
                    images: List<String>, stock: Int) {

        val product = Product(
            id, productName, category, price, offer,
            description, color, sizes, images, stock
        )

        viewModelScope.launch {
            _editProduct.emit(Resource.Loading())
        }

        firestore.collection("products").document(id)
            .set(product, SetOptions.merge()) // Correctly updates the product
            .addOnSuccessListener {
                viewModelScope.launch {
                    _editProduct.emit(Resource.Success(product))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _editProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}