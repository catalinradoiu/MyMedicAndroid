import android.app.Application
import com.catalin.mymedic.component.ApplicationComponent

/**
 * @author catalinradoiu
 * @since 2/6/2018
 */
class MyMedicApplication : Application() {

    override fun onCreate() {
        super.onCreate()

    }

    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }
}