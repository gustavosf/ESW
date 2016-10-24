class SessionController < ApplicationController

  skip_before_filter :require_login

  def new
    @users = User.all
  end

  def create
    user = User.find(params[:user_id])
    if user && user.authenticate(params[:password])
      session[:user_id] = user.id
      user.last_access = Time.now
      user.save
      
      redirect_to root_url
    else
      flash.now[:error] = "Senha inválida!"
      @users = User.all
      render "new"
    end
  end

  def destroy
    session[:user_id] = nil
    redirect_to login_url, :notice => "Você foi deslogado"
  end

end