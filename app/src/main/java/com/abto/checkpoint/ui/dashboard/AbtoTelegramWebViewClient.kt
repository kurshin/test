package com.abto.checkpoint.ui.dashboard

import android.webkit.ClientCertRequest
import android.webkit.HttpAuthHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

const val TELEGRAM_PHONE = "380" + "-your phone-"
class AbtoTelegramWebViewClient: WebViewClient() {

    private val jsLogin = """
    setTimeout(function() {
        var element = document.querySelector('.input-field-phone .input-field-input');
        
        if (element) {
            element.textContent = '+$TELEGRAM_PHONE';
            var event = new Event('input', {
                bubbles: true,
                cancelable: true,
            });
            element.dispatchEvent(event);
            console.log('Текстове поле заповнено:', element.textContent);
        } else {
            console.log('Элемент з класом input-field-input не знайдений');
        }
    }, 2500);
    """

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.evaluateJavascript(jsLogin, null)
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView?,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
        super.onReceivedClientCertRequest(view, request)
    }

    

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
    }
}