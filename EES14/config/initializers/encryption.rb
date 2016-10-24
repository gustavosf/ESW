
SymmetricEncryption.load!('config/symmetric-encryption.yml', 'development')
Mongoid::EncryptedFields.cipher = SymmetricEncryption