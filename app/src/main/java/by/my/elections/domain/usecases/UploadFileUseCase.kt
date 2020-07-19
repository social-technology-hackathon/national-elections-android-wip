package by.my.elections.domain.usecases

import by.my.elections.domain.repository.FirebaseRepository
import io.reactivex.Completable
import java.io.InputStream

class UploadFileUseCase(private val firebaseRepository: FirebaseRepository) :
    BaseCompletableUseCaseWithParam<ByteArray> {

    override fun execute(param: ByteArray): Completable {
        return firebaseRepository.uploadStream(param)
    }
}