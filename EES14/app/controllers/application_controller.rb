class ApplicationController < ActionController::Base
  protect_from_forgery with: :exception
  #before_filter :require_login

  private 

  def require_login
    redirect_to login_url unless current_user
  end

  def current_user
    @current_user ||= User.get_current
  end

  helper_method :current_user
end
