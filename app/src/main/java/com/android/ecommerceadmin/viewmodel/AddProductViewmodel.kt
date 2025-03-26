package com.android.ecommerceadmin.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ecommerceadmin.data.Product
import com.android.ecommerceadmin.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddProductViewmodel @Inject constructor(
    val firestore: FirebaseFirestore
) : ViewModel() {

    private val _productName = MutableLiveData<String>()
    val productName = _productName
    fun setProductName(productName: String) {
        _productName.value = productName
    }

    private val _category = MutableLiveData<String>()
    val category = _category
    fun setCategory(category: String) {
        _category.value = category
    }


    private val _productDescription = MutableLiveData<String>()
    val productDescription = _productDescription
    fun getProductDescription(productDescription : String){
        _productDescription.value = productDescription
    }

    private val _price = MutableLiveData<Float>()
    val price = _price
    fun getPrice(price : Float){
        _price.value = price
    }

    private val _offer = MutableLiveData<Float>()
    val offer = _offer
    fun getOffer(offer : Float){
        _offer.value = offer
    }

    private val _sizes = MutableLiveData<String>()
    val sizes = _sizes
    fun getSizes(sizes : String) {
        _sizes.value = sizes
    }

    private val _color = MutableLiveData<List<String>>()
    val color = _color
    fun getColor(color : List<String>){
        _color.value = color
    }

    private val _images = MutableLiveData<List<String>>()
    val images = _images
    fun getImages(images : List<String>){
        _images.value = images
    }

    private val _stock = MutableLiveData<Int>()
    val stock = _stock
    fun getStock(stock : Int){
        _stock.value = stock
    }

    private val _saveProduct = MutableStateFlow<Resource<Product>>(Resource.Undefined())
    val saveProduct = _saveProduct.asSharedFlow()

    fun saveProduct(productName: String,category: String,price : Float,
                    offer:Float? = null,description: String? = null,
                    color : List<Int>?= null,sizes: List<String>? = null,
                    images: List<String>,stock : Int){
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
        firestore.collection("product").add(product).addOnSuccessListener {
            viewModelScope.launch {
                _saveProduct.emit(Resource.Success(product))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _saveProduct.emit(Resource.Error(it.message.toString()))
            }
        }
    }

}