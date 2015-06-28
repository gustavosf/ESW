<?php

class Application {
	
	protected $_services = [];

	public function register($method, $service, $callback)
	{
		$this->_services[$method][$service] = $callback;
	}

	public function process()
	{
		if (isset($this->_services[$method]) and
		    isset($this->_services[$method][$service])) {
				
		}
	}

	public function run()
	{
		$method = $_SERVER["REQUEST_METHOD"];
		$service = $_SERVER["REQUEST_URI"];

		$return = null;

		if (isset($this->_services[$method])) {
			foreach ($this->_services[$method] as $route => $callback) {
				if (preg_match('{'.$route.'}', $service, $match)) {
					$return = call_user_func_array($callback, array_slice($match, 1));
				}
			}
		}

		if ($return === null) echo "Invalid service call";
		else echo json_encode($return);
	}

}
