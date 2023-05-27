<?php

class RestEndpoint
{
    public function process()
    {
        $this->setupHeader();
    
        switch ($this->method()) {
        case 'GET':
            $this->getData();
            break;
            
        case 'POST':
            $this->postData();
            break;
            
        case 'PUT':
            $this->putData();
            break;
            
        case 'DELETE':
            $this->deleteData();
            break;
        }
    }
    protected function setupHeader()
    {}
    
    protected function getData()
    {
        echo 'Geting...';
    }
    
    protected function postData()
    {
        echo 'Posting...';
    }
    
    protected function putData()
    {
        echo 'Putting...';
    }
    
    protected function deleteData()
    {
        echo 'Deleting...';
    }
    
    public function method()
    {return $_SERVER['REQUEST_METHOD'];}

    public function path($index)
    {return $this->paths()[$index + 1];}
    
    public function pathCount()
    {return count($this->paths()) - 1;}
    
    public function paths()
    {return explode('/', $_SERVER['PATH_INFO']);}
    
    public function query($name)
    {
        for ($i = 0; $i < $this->queryCount(); $i++) {
            $q = $this->queryAt($i);
            if ($q['name'] == $name)
                return $q['value'];
        }
        
        return null;
    }
    
    public function queryAt($index)
    {
        $tmp = explode('=', $this->queries()[$index]);
        $q = array();
        $q['name'] = $tmp[0];
        $q['value'] = $tmp[1];
        return $q;
    }
    
    public function queryCount()
    {return count($this->queries());}
    
    public function queries()
    {return explode('&', $_SERVER['QUERY_STRING']);}
}

?>
