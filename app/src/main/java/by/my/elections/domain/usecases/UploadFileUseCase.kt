package by.my.elections.domain.usecases

import by.my.elections.domain.repository.FirebaseRepository
import io.reactivex.Completable
import org.opencv.core.Mat
import java.io.InputStream

class UploadFileUseCase(private val firebaseRepository: FirebaseRepository) :
    BaseCompletableUseCaseWithParam<Mat> {

    override fun execute(param: Mat): Completable {
        return firebaseRepository.uploadStream(param)
    }
}