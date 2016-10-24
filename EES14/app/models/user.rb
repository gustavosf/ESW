class User
  include Mongoid::Document
  include Mongoid::Timestamps # Inclui e treta os campos created_at e updated_at

  field :email, type: String
  field :password, type: Mongoid::EncryptedString
  field :name, type: String
  field :access, type: String
  field :active, type: Integer, default: 1
  field :last_login, type: DateTime

  validates_presence_of :email
  validates_presence_of :name
  validates_presence_of :access

  before_create :generate_default_password

  def self.get_current
  	User.find_by(email: session[:user_id]) if defined? session[:user_id]
  end

  protected

  def generate_default_password
    # Vamos usar o email como password default por enquanto
    self.password = email
  end

end
